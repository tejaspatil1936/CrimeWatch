package com.crimewatch.repository;

import com.crimewatch.entity.User;
import com.crimewatch.enums.Role;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(String userId);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    List<User> findByRole(Role role);

    List<User> findAll();

    void deleteById(String userId);

    long count();
}
