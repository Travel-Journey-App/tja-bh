package com.tja.bh.config.jwt;


import com.tja.bh.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static com.tja.bh.config.jwt.JwtFilter.AUTHORIZATION;
import static com.tja.bh.config.jwt.JwtFilter.BEARER_PREFIX;
import static com.tja.bh.config.jwt.JwtProvider.Status.BROKEN;
import static com.tja.bh.config.jwt.JwtProvider.Status.EXPIRED;
import static java.util.Objects.nonNull;

@Slf4j
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    private final JwtProvider jwtProvider;
    private final IUserService userRepository;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, JwtProvider jwtProvider,
                                  IUserService userService) {
        super(authenticationManager);
        this.jwtProvider = jwtProvider;
        this.userRepository = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        val header = req.getHeader(AUTHORIZATION);

        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        val authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        val headerToken = request.getHeader(AUTHORIZATION);
        if (nonNull(headerToken)) {
            val token = headerToken.replace(BEARER_PREFIX, "");
            val validationResult = jwtProvider.validateToken(token);
            if (!BROKEN.equals(validationResult)) {
                val email = jwtProvider.getLoginFromToken(token);
                val user = userRepository.findUserByEmail(email);

                if (EXPIRED.equals(validationResult)) {
                    log.debug("JwtFilter. Renewing expired token");
                    user.setSecret(jwtProvider.generateToken(email));
                }

                if (user != null) {
                    return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                }
            }
        }

        return null;
    }
}