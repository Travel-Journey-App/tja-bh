package com.tja.bh.auth.persistence.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jboss.aerogear.security.otp.api.Base32;

import javax.persistence.*;
import java.io.Serializable;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "user_account")
public class User implements Serializable {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;

    private String lastName;

    @EqualsAndHashCode.Include
    private String email;

    @Column(length = 60)
    private String password;

    private boolean enabled;

    private boolean isUsing2FA;

    private String secret;

    public User() {
        super();
        this.secret = Base32.random();
        this.enabled = false;
    }

}