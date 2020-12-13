package com.tja.bh.controller;

import com.tja.bh.auth.error.UserAlreadyExistException;
import com.tja.bh.dto.GenericResponse;
import com.tja.bh.dto.UserDto;
import com.tja.bh.persistence.model.User;
import com.tja.bh.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/auth", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@Slf4j
public class RegistrationController {
    private final IUserService userService;

    @Autowired
    public RegistrationController(IUserService userService) {
        super();
        this.userService = userService;
    }

    @PostMapping("/registration")
    public GenericResponse<User> register(@RequestBody UserDto userDto) {
        log.debug("Registering user account with information: {}", userDto);

        if (!userDto.getPassword().equals(userDto.getMatchingPassword())) {
            log.error("Registration failed. Password does not match: {}", userDto);
            return GenericResponse.error("Password does not match");
        }

        try {
            val user = userService.signUp(userDto);
            log.debug("Registration completed. User saved: {}", userDto);
            return GenericResponse.success(user);
        } catch (UserAlreadyExistException e) {
            log.error("Registration failed: {}", e.getMessage());
            return GenericResponse.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public GenericResponse<User> login(@RequestBody final UserDto userDto) {
        log.debug("Login user with information: {}", userDto);
        try {
            val existingUser = userService.signIn(userDto);
            return GenericResponse.success(existingUser);
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            return GenericResponse.error(e.getMessage());
        }
    }

    @PostMapping("/oauth")
    public GenericResponse<User> oauthLogin(@RequestBody final UserDto userDto) {
        log.debug("OAuth login user with information: {}", userDto);
        val oauthToken = userDto.getGoogleOAuthToken();
        try {
            return GenericResponse.success(userService.oauthSignIn(oauthToken));
        } catch (Exception e) {
            log.error("OAuth login failed: {}", e.getMessage());
            return GenericResponse.error(e.getMessage());
        }
    }
}