package com.tja.bh.controller;

import com.tja.bh.dto.GenericResponse;
import com.tja.bh.persistence.model.User;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/user", produces = APPLICATION_JSON_VALUE)
@Slf4j
public class UserController {

    @GetMapping("")
    public GenericResponse<User> getUser() {
        val auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            val user = (User) auth.getPrincipal();
            return GenericResponse.success(user);
        } catch (Exception e) {
            log.error("UserController. Exception occurred while retrieving current user: ", e);
            return GenericResponse.error();
        }
    }
}
