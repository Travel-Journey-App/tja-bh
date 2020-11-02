package com.tja.bh.auth.service;

public interface ISecurityUserService {

    String validatePasswordResetToken(String token);

}
