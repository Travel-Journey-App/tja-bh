package com.tja.bh.auth.controller;

import com.tja.bh.auth.GenericResponse;
import com.tja.bh.auth.dto.UserDto;
import com.tja.bh.auth.error.UserAlreadyExistException;
import com.tja.bh.auth.persistence.model.User;
import com.tja.bh.auth.service.ISecurityUserService;
import com.tja.bh.auth.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(value = "/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RestController
@Slf4j
public class RegistrationController {
    private final IUserService userService;
    private final ISecurityUserService securityUserService;
    private final MessageSource messages;

    @Autowired
    public RegistrationController(IUserService userService, ISecurityUserService securityUserService, MessageSource messages) {
        super();
        this.userService = userService;
        this.securityUserService = securityUserService;
        this.messages = messages;
    }

    @PostMapping("/registration")
    public GenericResponse<User> register(@RequestBody UserDto userDto) {
        log.debug("Registering user account with information: {}", userDto);

        if (!userDto.getPassword().equals(userDto.getMatchingPassword())) {
            log.error("Registration failed. Password does not match: {}", userDto);
            return GenericResponse.error();
        }

        try {
            val user = userService.signUp(userDto);
            log.debug("Registration completed. User saved: {}", userDto);
            return GenericResponse.success(user);
        } catch (UserAlreadyExistException e) {
            log.error("Registration failed: {}", e.getMessage());
            return GenericResponse.error();
        }
    }

    @PostMapping("/login")
    public GenericResponse<User> login(@RequestBody final UserDto userDto) {
        log.debug("Login user with information: {}", userDto);
        try {
            val existingUser = userService.findUserByEmail(userDto.getEmail());
            return GenericResponse.success(existingUser);
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            return GenericResponse.error();
        }
    }
}