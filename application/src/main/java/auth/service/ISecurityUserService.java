package auth.service;

public interface ISecurityUserService {

    String validatePasswordResetToken(String token);

}
