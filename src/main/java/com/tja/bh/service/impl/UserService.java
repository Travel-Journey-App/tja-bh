package com.tja.bh.service.impl;

import com.tja.bh.auth.error.UserAlreadyExistException;
import com.tja.bh.config.jwt.JwtProvider;
import com.tja.bh.dto.UserDto;
import com.tja.bh.persistence.model.User;
import com.tja.bh.persistence.model.enumeration.UserRole;
import com.tja.bh.persistence.repository.UserRepository;
import com.tja.bh.service.IUserService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;

import static java.util.Objects.isNull;

@Service
@Transactional
public class UserService implements IUserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    @Autowired
    public UserService(@Lazy UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder, @Lazy JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    // API

    @Override
    public User signUp(final UserDto userDto) {
        if (emailExists(userDto.getEmail())) {
            throw new UserAlreadyExistException("There is an account with that email address: " + userDto.getEmail());
        }

        val user = userRepository.save(
                User.builder()
                        .firstName(userDto.getFirstName())
                        .lastName(userDto.getLastName())
                        .password(passwordEncoder.encode(userDto.getPassword()))
                        .email(userDto.getEmail())
                        .isUsing2FA(userDto.isUsing2FA())
                        .roles(Collections.singleton(UserRole.USER.convertToRole()))
                        .build());

        val token = jwtProvider.generateToken(user.getEmail());
        user.setSecret(token);
        return user;
    }

    @Override
    public User signIn(final UserDto accountDto) throws UsernameNotFoundException {
        val user = findUserByEmail(accountDto.getEmail());

        if (isNull(user)) {
            throw new UsernameNotFoundException("Username was not found");
        }

        if (!passwordEncoder.matches(accountDto.getPassword(), user.getPassword())) {
            throw new AuthenticationCredentialsNotFoundException("Incorrect credentials");
        }

        if (!user.isEnabled()) {
            user.setEnabled(true);
            userRepository.save(user);
        }

        val token = jwtProvider.generateToken(user.getEmail());
        user.setSecret(token);
        return user;
    }

    @Override
    public User getUser(final String verificationToken) {
        val email = jwtProvider.getLoginFromToken(verificationToken);
        return userRepository.findByEmail(email);
    }

    @Override
    public void deleteUser(final User user) {
        userRepository.delete(user);
    }

    @Override
    public User findUserByEmail(final String email) {
        return userRepository.findByEmail(email);
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