package com.crimewatch.service;

import com.crimewatch.entity.User;
import com.crimewatch.enums.Role;
import com.crimewatch.repository.UserRepository;
import com.crimewatch.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    public User register(String username, String email, String rawPassword, Role role) {
        if (userRepo.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already taken: " + username);
        }
        if (userRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }
        User user = new User();
        user.setUserId(IdGenerator.userId());
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setEnabled(true);
        user.setCreatedAt(System.currentTimeMillis());
        return userRepo.save(user);
    }

    public Optional<User> findById(String userId) {
        return userRepo.findById(userId);
    }

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public List<User> findByRole(Role role) {
        return userRepo.findByRole(role);
    }

    public void changeRole(String userId, Role newRole) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        user.setRole(newRole);
        userRepo.save(user);
    }

    public void deleteById(String userId) {
        userRepo.deleteById(userId);
    }
}
