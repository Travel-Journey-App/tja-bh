package com.tja.bh.auth.service.impl;

import com.maxmind.geoip2.DatabaseReader;
import com.tja.bh.auth.dto.UserDto;
import com.tja.bh.auth.error.UserAlreadyExistException;
import com.tja.bh.auth.model.PasswordResetToken;
import com.tja.bh.auth.model.User;
import com.tja.bh.auth.model.VerificationToken;
import com.tja.bh.auth.repository.PasswordResetTokenRepository;
import com.tja.bh.auth.repository.UserRepository;
import com.tja.bh.auth.repository.VerificationTokenRepository;
import com.tja.bh.auth.service.IUserService;
import lombok.val;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Transactional
public class UserService implements IUserService {

    public static final String TOKEN_INVALID = "invalidToken";
    public static final String TOKEN_EXPIRED = "expired";
    public static final String TOKEN_VALID = "valid";
    public static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    public static String APP_NAME = "SpringRegistration";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationTokenRepository tokenRepository;
    @Autowired
    private PasswordResetTokenRepository passwordTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SessionRegistry sessionRegistry;
    @Autowired
    @Qualifier("GeoIPCountry")
    private DatabaseReader databaseReader;
    @Autowired
    private Environment env;

    // API

    @Override
    public User registerNewUserAccount(final UserDto accountDto) {
        if (emailExists(accountDto.getEmail())) {
            throw new UserAlreadyExistException("There is an account with that email address: " + accountDto.getEmail());
        }

        val user = new User();
        user.setFirstName(accountDto.getFirstName());
        user.setLastName(accountDto.getLastName());
        user.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        user.setEmail(accountDto.getEmail());
        user.setUsing2FA(accountDto.isUsing2FA());

        return userRepository.save(user);
    }

    @Override
    public User getUser(final String verificationToken) {
        val token = tokenRepository.findByToken(verificationToken);
        return nonNull(token) ? token.getUser() : null;
    }

    @Override
    public void saveRegisteredUser(final User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteUser(final User user) {
        val verificationToken = tokenRepository.findByUser(user);
        if (nonNull(verificationToken)) {
            tokenRepository.delete(verificationToken);
        }

        val passwordToken = passwordTokenRepository.findByUser(user);
        if (nonNull(passwordToken)) {
            passwordTokenRepository.delete(passwordToken);
        }

        userRepository.delete(user);
    }

    @Override
    public void createVerificationTokenForUser(final User user, final String token) {
        val myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }

    @Override
    public VerificationToken getVerificationToken(final String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    @Override
    public VerificationToken generateNewVerificationToken(final String existingVerificationToken) {
        val vToken = tokenRepository.findByToken(existingVerificationToken);
        vToken.updateToken(UUID.randomUUID()
                .toString());

        return tokenRepository.save(vToken);
    }

    @Override
    public void createPasswordResetTokenForUser(final User user, final String token) {
        val myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
    }

    @Override
    public User findUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public PasswordResetToken getPasswordResetToken(final String token) {
        return passwordTokenRepository.findByToken(token);
    }

    @Override
    public Optional<User> getUserByPasswordResetToken(final String token) {
        return Optional.ofNullable(passwordTokenRepository.findByToken(token).getUser());
    }

    @Override
    public Optional<User> getUserByID(final long id) {
        return userRepository.findById(id);
    }

    @Override
    public void changeUserPassword(final User user, final String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    public boolean checkIfValidOldPassword(final User user, final String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    @Override
    public String validateVerificationToken(String token) {
        val verificationToken = tokenRepository.findByToken(token);
        if (isNull(verificationToken)) {
            return TOKEN_INVALID;
        }

        val user = verificationToken.getUser();
        val cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate()
                .getTime() - cal.getTime()
                .getTime()) <= 0) {
            tokenRepository.delete(verificationToken);
            return TOKEN_EXPIRED;
        }

        user.setEnabled(true);
        // tokenRepository.delete(verificationToken);
        userRepository.save(user);
        return TOKEN_VALID;
    }

    @Override
    public String generateQRUrl(User user) throws UnsupportedEncodingException {
        return QR_PREFIX + URLEncoder.encode(String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", APP_NAME, user.getEmail(), user.getSecret(), APP_NAME), "UTF-8");
    }

    @Override
    public User updateUser2FA(boolean use2FA) {
        val curAuth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (User) curAuth.getPrincipal();
        currentUser.setUsing2FA(use2FA);
        currentUser = userRepository.save(currentUser);

        val auth = new UsernamePasswordAuthenticationToken(currentUser, currentUser.getPassword(), curAuth.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        return currentUser;
    }

    @Override
    public List<String> getUsersFromSessionRegistry() {
        return sessionRegistry.getAllPrincipals()
                .stream()
                .filter(u -> !sessionRegistry.getAllSessions(u, false).isEmpty())
                .map(o -> {
                    if (o instanceof User) {
                        return ((User) o).getEmail();
                    } else {
                        return o.toString();
                    }
                })
                .collect(Collectors.toList());
    }

    private boolean emailExists(final String email) {
        return userRepository.findByEmail(email) != null;
    }

}