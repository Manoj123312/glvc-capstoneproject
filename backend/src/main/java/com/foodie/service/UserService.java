package com.foodie.service;

import com.foodie.dto.Dtos.UserDto;
import com.foodie.dto.Dtos.UserUpdateRequest;
import com.foodie.entity.User;
import com.foodie.exception.BadRequestException;
import com.foodie.exception.NotFoundException;
import com.foodie.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository users;

    public UserService(UserRepository users) { this.users = users; }

    public List<UserDto> findAll() { return users.findAll().stream().map(UserDto::from).toList(); }

    public UserDto findById(Long id) { return UserDto.from(get(id)); }

    public UserDto findByEmail(String email) {
        return UserDto.from(users.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found")));
    }

    public UserDto update(Long id, UserUpdateRequest req) {
        User u = get(id);
        u.setName(req.name());
        u.setPhone(req.phone());
        u.setAddress(req.address());
        if (req.role() != null) u.setRole(req.role());
        if (req.active() != null) u.setActive(req.active());
        return UserDto.from(users.save(u));
    }

    public UserDto updateProfile(String email, UserUpdateRequest req) {
        User u = users.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        u.setName(req.name());
        u.setPhone(req.phone());
        u.setAddress(req.address());
        return UserDto.from(users.save(u));
    }

    public void delete(Long id, String currentEmail) {
        User u = get(id);
        if (u.getEmail().equalsIgnoreCase(currentEmail)) throw new BadRequestException("You cannot delete your own account");
        users.delete(u);
    }

    private User get(Long id) {
        return users.findById(id).orElseThrow(() -> new NotFoundException("User " + id + " not found"));
    }
}
