package com.foodie.security;

import com.foodie.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository users;

    public AppUserDetailsService(UserRepository users) { this.users = users; }

    @Override
    public UserDetails loadUserByUsername(String email) {
        com.foodie.entity.User u = users.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return User.withUsername(u.getEmail()).password(u.getPassword()).roles(u.getRole().name())
                .disabled(!u.isActive()).build();
    }
}
