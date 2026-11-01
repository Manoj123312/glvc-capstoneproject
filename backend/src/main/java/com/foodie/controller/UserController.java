package com.foodie.controller;

import com.foodie.dto.Dtos.UserDto;
import com.foodie.dto.Dtos.UserUpdateRequest;
import com.foodie.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Users")
public class UserController {
    private final UserService users;

    public UserController(UserService users) { this.users = users; }

    // ---- Admin: user management ----
    @GetMapping("/api/admin/users")
    public List<UserDto> all() { return users.findAll(); }

    @GetMapping("/api/admin/users/{id}")
    public UserDto one(@PathVariable Long id) { return users.findById(id); }

    @PutMapping("/api/admin/users/{id}")
    public UserDto update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest req) { return users.update(id, req); }

    @DeleteMapping("/api/admin/users/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        users.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    // ---- Logged-in user: own profile ----
    @GetMapping("/api/users/me")
    public UserDto me(Principal principal) { return users.findByEmail(principal.getName()); }

    @PutMapping("/api/users/me")
    public UserDto updateMe(Principal principal, @Valid @RequestBody UserUpdateRequest req) {
        return users.updateProfile(principal.getName(), req);
    }
}
