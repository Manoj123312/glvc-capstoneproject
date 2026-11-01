package com.foodie.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    private final AppUserDetailsService details;

    public JwtAuthFilter(JwtService jwt, AppUserDetailsService details) {
        this.jwt = jwt;
        this.details = details;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails ud = details.loadUserByUsername(jwt.extractEmail(header.substring(7)));
                if (ud.isEnabled()) {
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities()));
                }
            } catch (Exception ignored) {
                // invalid/expired token: request continues unauthenticated and is rejected by security rules
            }
        }
        chain.doFilter(req, res);
    }
}
