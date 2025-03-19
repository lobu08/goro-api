package com.goro.goro.api.secutiry;

import com.goro.goro.api.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        logger.debug("AuthTokenFilter.doFilterInternal() called"); // Log: Filter started

        try {
            String jwt = parseJwt(request);
            logger.debug("JWT parsed from request: {}", jwt); // Log: JWT value

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                logger.debug("JWT is not null and JWT is Valid"); // Log: JWT not null and VALID

                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                logger.debug("Username extracted from JWT: {}", username); // Log: Username extracted

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                logger.debug("UserDetails loaded for username: {}", username); // Log: UserDetails loaded

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                        userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Authentication set in SecurityContextHolder for user: {}", username); // Log: Authentication set

            } else {
                logger.warn("JWT is null or JWT is Invalid"); // Log: JWT null OR invalid
            }

        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
        logger.debug("AuthTokenFilter.doFilterInternal() completed"); // Log: Filter finished
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        logger.debug("Authorization Header: {}", headerAuth); // Log: Header value

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            String token = headerAuth.substring(7, headerAuth.length());
            logger.debug("Extracted token: {}", token); // Log: Extracted Token
            return token;
        }

        logger.warn("Authorization header is missing or does not start with Bearer"); // Log: header missing or wrong format
        return null;
    }
}