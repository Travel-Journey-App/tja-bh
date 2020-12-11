package com.tja.bh.config.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
@Slf4j
public class JwtProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String generateToken(String login) {
        Date date = Date.from(LocalDate.now().plusDays(15).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(login)
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public Status validateToken(String token) throws ExpiredJwtException {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return Status.OK;
        } catch (ExpiredJwtException expEx) {
            log.debug("Token expired");
            return Status.EXPIRED;
        } catch (UnsupportedJwtException unsEx) {
            log.debug("Unsupported jwt");
        } catch (MalformedJwtException mjEx) {
            log.debug("Malformed jwt");
        } catch (SignatureException sEx) {
            log.debug("Invalid signature");
        } catch (Exception e) {
            log.debug("invalid token");
        }

        return Status.BROKEN;
    }

    public String getLoginFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public enum Status {
        OK,
        EXPIRED,
        BROKEN
    }
}