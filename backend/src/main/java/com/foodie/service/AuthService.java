package com.foodie.service;

import com.foodie.dto.Dtos.*;
import com.foodie.entity.User;
import com.foodie.exception.BadRequestException;
import com.foodie.repository.UserRepository;
import com.foodie.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, AuthenticationManager authManager, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwt = jwt;
    }

    public AuthResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (users.existsByEmail(email)) throw new BadRequestException("Email already registered");
        User u = new User();
        u.setName(req.name());
        u.setEmail(email);
        u.setPassword(encoder.encode(req.password()));
        u.setPhone(req.phone());
        u.setAddress(req.address());
        u.setRole(User.Role.USER);
        return toResponse(users.save(u));
    }

    public AuthResponse login(LoginRequest req) {
        String email = req.email().trim().toLowerCase();
        authManager.authenticate(new UsernamePasswordAuthenticationToken(email, req.password()));
        return toResponse(users.findByEmail(email).orElseThrow());
    }

    private AuthResponse toResponse(User u) {
        return new AuthResponse(jwt.generate(u), u.getId(), u.getName(), u.getEmail(), u.getRole().name());
    }
}
