package com.tja.bh.service;

import com.tja.bh.dto.UserDto;
import com.tja.bh.auth.error.UserAlreadyExistException;
import com.tja.bh.persistence.model.PasswordResetToken;
import com.tja.bh.persistence.model.User;
import com.tja.bh.persistence.model.VerificationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

public interface IUserService extends UserDetailsService {

    User signUp(UserDto accountDto) throws UserAlreadyExistException;

    User signIn(UserDto accountDto) throws UsernameNotFoundException;

    User getUser();

    User getUser(String verificationToken);

    void deleteUser(User user);

    User findUserByEmail(String email);
}
