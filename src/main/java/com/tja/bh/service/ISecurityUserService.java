package com.tja.bh.service;

public interface ISecurityUserService {

    String validatePasswordResetToken(String token);

}
