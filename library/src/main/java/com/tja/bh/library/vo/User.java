package com.tja.bh.library.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Builder
@Data
public class User {
    Long id;
    String firstName;
    String lastName;
    String username;
    String password;
    String gmailAddress;
    String mobilePhone;
    Date dateOfBirth;
    @Builder.Default
    Boolean isSyncEnabled = false;
    List<Trip> upcomingTrips;
    List<Trip> archiveTrips;
}
