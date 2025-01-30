package com.shelfscape.service;

import com.shelfscape.model.Role;
import com.shelfscape.model.User;
import com.shelfscape.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService; // Usługa do zarządzania rolami

    public User registerUser(User user) {
        // Sprawdzamy, czy użytkownik już istnieje
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email is already in use.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleService.getRoleByName("USER");
        user.setRoles(Collections.singleton(userRole));

        return userRepository.save(user);
    }

    public String authenticate(User user) {
        return "Login successful";
    }
}
