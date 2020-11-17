package com.tja.bh.auth.controller;

import com.tja.bh.auth.service.ISecurityUserService;
import com.tja.bh.auth.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(value = "/api", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RestController
@Slf4j
public class MobileRestController {
    private final IUserService userService;
    private final ISecurityUserService securityUserService;
    private final MessageSource messages;
    private final JavaMailSender mailSender;
    private final ApplicationEventPublisher eventPublisher;
    private final String emailSupport;

    @Autowired
    public MobileRestController(IUserService userService, ISecurityUserService securityUserService,
                                MessageSource messages, JavaMailSender mailSender,
                                ApplicationEventPublisher eventPublisher,
                                @Value("${support.email.address}") String emailSupport) {
        this.userService = userService;
        this.securityUserService = securityUserService;
        this.messages = messages;
        this.mailSender = mailSender;
        this.eventPublisher = eventPublisher;
        this.emailSupport = emailSupport;
    }
}