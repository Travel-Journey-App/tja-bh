package com.tja.bh.auth.service;

import com.tja.bh.auth.dto.UserDto;
import com.tja.bh.auth.error.UserAlreadyExistException;
import com.tja.bh.auth.persistence.model.PasswordResetToken;
import com.tja.bh.auth.persistence.model.User;
import com.tja.bh.auth.persistence.model.VerificationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

public interface IUserService extends UserDetailsService {

    User signUp(UserDto accountDto) throws UserAlreadyExistException;

    User signIn(UserDto accountDto) throws UsernameNotFoundException;

    User getUser(String verificationToken);

    void saveRegisteredUser(User user);

    void deleteUser(User user);

    void createVerificationTokenForUser(User user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

    VerificationToken generateNewVerificationToken(String token);

    void createPasswordResetTokenForUser(User user, String token);

    User findUserByEmail(String email);

    PasswordResetToken getPasswordResetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    Optional<User> getUserByID(long id);

    void changeUserPassword(User user, String password);

    boolean checkIfValidOldPassword(User user, String password);

    String validateVerificationToken(String token);

    String generateQRUrl(User user) throws UnsupportedEncodingException;

    User updateUser2FA(boolean use2FA);

    List<String> getUsersFromSessionRegistry();
}
