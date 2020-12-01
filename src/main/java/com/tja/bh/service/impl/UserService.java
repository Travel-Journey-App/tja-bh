package com.tja.bh.service.impl;

import com.tja.bh.auth.error.UserAlreadyExistException;
import com.tja.bh.dto.UserDto;
import com.tja.bh.persistence.model.PasswordResetToken;
import com.tja.bh.persistence.model.User;
import com.tja.bh.persistence.model.VerificationToken;
import com.tja.bh.persistence.model.enumeration.UserRole;
import com.tja.bh.persistence.repository.PasswordResetTokenRepository;
import com.tja.bh.persistence.repository.UserRepository;
import com.tja.bh.persistence.repository.VerificationTokenRepository;
import com.tja.bh.service.IUserService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
    public static String APP_NAME = "TjaRegistration";

    private final UserRepository userRepository;

    private final VerificationTokenRepository tokenRepository;

    private final PasswordResetTokenRepository passwordTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final SessionRegistry sessionRegistry;

    @Autowired
    public UserService(UserRepository userRepository, VerificationTokenRepository tokenRepository, PasswordResetTokenRepository passwordTokenRepository, PasswordEncoder passwordEncoder, SessionRegistry sessionRegistry) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordTokenRepository = passwordTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionRegistry = sessionRegistry;
    }

    // API

    @Override
    public User signUp(final UserDto userDto) {
        if (emailExists(userDto.getEmail())) {
            throw new UserAlreadyExistException("There is an account with that email address: " + userDto.getEmail());
        }

        val user = User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .email(userDto.getEmail())
                .isUsing2FA(userDto.isUsing2FA())
                .roles(Collections.singleton(UserRole.USER.convertToRole()))
                .build();

        return userRepository.save(user);
    }

    @Override
    public User signIn(UserDto accountDto) throws UsernameNotFoundException {
        val user = findUserByEmail(accountDto.getEmail());

        if (isNull(user)) {
            throw new UsernameNotFoundException("Username was not found");
        }

        if (!passwordEncoder.matches(accountDto.getPassword(), user.getPassword())) {
            throw new AuthenticationCredentialsNotFoundException("Incorrect credentials");
        }

        return user;
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
    public String generateQRUrl(User user) {
        return QR_PREFIX + URLEncoder.encode(String.format("oauth://totp/%s:%s?secret=%s&issuer=%s", APP_NAME, user.getEmail(), user.getSecret(), APP_NAME), StandardCharsets.UTF_8);
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        val user = userRepository.findByEmail(username);

        if (isNull(user)) {
            throw new UsernameNotFoundException("User was not found");
        }

        return user;
    }
}