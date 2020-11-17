package com.tja.bh.auth;

import com.tja.bh.auth.persistence.model.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

@Getter
@EqualsAndHashCode(callSuper = true)
public class OnRegistrationCompleteEvent extends ApplicationEvent implements Serializable {
    //private final String appUrl;
    //private final Locale locale;
    private final User user;

    public OnRegistrationCompleteEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}