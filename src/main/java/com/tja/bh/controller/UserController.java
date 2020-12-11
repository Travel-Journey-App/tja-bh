package com.tja.bh.controller;

import com.tja.bh.dto.GenericResponse;
import com.tja.bh.persistence.model.User;
import com.tja.bh.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/user", produces = APPLICATION_JSON_VALUE)
@Slf4j
public class UserController {

    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public GenericResponse<User> getUser() {
        try {
            return GenericResponse.success(userService.getUser());
        } catch (Exception e) {
            log.error("UserController. Exception occurred while retrieving current user: ", e);
            return GenericResponse.error(e.getMessage());
        }
    }
}
