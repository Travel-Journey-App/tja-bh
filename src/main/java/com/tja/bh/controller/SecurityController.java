package com.tja.bh.controller;

import com.tja.bh.persistence.model.ActiveUserStore;
import com.tja.bh.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Locale;

@Controller
@Slf4j
public class SecurityController {
    final ActiveUserStore activeUserStore;
    final IUserService userService;

    @Autowired
    public SecurityController(ActiveUserStore activeUserStore, IUserService userService) {
        this.activeUserStore = activeUserStore;
        this.userService = userService;
    }

    @GetMapping("/loggedUsers")
    public String getLoggedUsers(final Locale locale, final Model model) {
        model.addAttribute("users", activeUserStore.getUsers());
        return "users";
    }

    @GetMapping("/loggedUsersFromSessionRegistry")
    public String getLoggedUsersFromSessionRegistry(final Locale locale, final Model model) {
        model.addAttribute("users", userService.getUsersFromSessionRegistry());
        return "users";
    }
}
