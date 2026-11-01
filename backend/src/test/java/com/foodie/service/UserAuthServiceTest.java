package com.foodie.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.foodie.dto.Dtos.AuthResponse;
import com.foodie.dto.Dtos.RegisterRequest;
import com.foodie.dto.Dtos.UserUpdateRequest;
import com.foodie.entity.User;
import com.foodie.exception.BadRequestException;
import com.foodie.repository.UserRepository;
import com.foodie.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserAuthServiceTest {
    @Mock UserRepository users;
    @Mock AuthenticationManager authManager;

    @Test
    void register_hashesPasswordAndIssuesToken() {
        JwtService jwt = new JwtService("unit-test-secret-key-that-is-long-enough-1234567890", 60000);
        AuthService auth = new AuthService(users, new BCryptPasswordEncoder(), authManager, jwt);
        when(users.existsByEmail("a@b.com")).thenReturn(false);
        when(users.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        AuthResponse res = auth.register(new RegisterRequest("A", "A@B.com", "secret1", null, null));

        assertEquals("USER", res.role());
        assertEquals("a@b.com", jwt.extractEmail(res.token()));
    }

    @Test
    void register_duplicateEmail_throws() {
        AuthService auth = new AuthService(users, new BCryptPasswordEncoder(), authManager,
                new JwtService("unit-test-secret-key-that-is-long-enough-1234567890", 60000));
        when(users.existsByEmail("a@b.com")).thenReturn(true);
        assertThrows(BadRequestException.class, () -> auth.register(new RegisterRequest("A", "a@b.com", "secret1", null, null)));
    }

    @Test
    void deleteOwnAccount_throws() {
        UserService service = new UserService(users);
        User u = new User();
        u.setId(1L);
        u.setEmail("admin@foodie.com");
        when(users.findById(1L)).thenReturn(Optional.of(u));
        assertThrows(BadRequestException.class, () -> service.delete(1L, "admin@foodie.com"));
        verify(users, never()).delete(any());
    }

    @Test
    void update_changesRoleAndActive() {
        UserService service = new UserService(users);
        User u = new User();
        u.setId(2L);
        u.setName("Old");
        u.setRole(User.Role.USER);
        when(users.findById(2L)).thenReturn(Optional.of(u));
        when(users.save(u)).thenReturn(u);

        var dto = service.update(2L, new UserUpdateRequest("New", "999", "addr", User.Role.ADMIN, false));

        assertEquals("New", dto.name());
        assertEquals("ADMIN", dto.role());
        assertFalse(dto.active());
    }
}
