package com.shelfscape.service;

import com.shelfscape.model.Role;
import com.shelfscape.model.User;
import com.shelfscape.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    public User registerUser(User user) {
        // Check if the user already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already in use.");
        }

        // Encrypt the user's password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Fetch the USER role and handle Optional
        Role userRole = roleService.getRoleByName("USER")
                .orElseThrow(() -> new RuntimeException("Role USER not found"));

        // Assign the role to the user
        user.setRoles(Collections.singleton(userRole));

        // Save the user to the database
        return userRepository.save(user);
    }

    // Add the findByEmail method to search for users by email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public String authenticate(User user) {
        return "Login successful";
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<User> searchUsers(String searchQuery) {
        return userRepository.findByEmailContainingOrFirstNameContainingOrLastNameContaining(searchQuery, searchQuery, searchQuery);
    }
}
