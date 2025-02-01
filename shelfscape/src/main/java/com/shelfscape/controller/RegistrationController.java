package com.shelfscape.controller;

import com.shelfscape.model.User;
import com.shelfscape.model.Role;
import com.shelfscape.service.UserService;
import com.shelfscape.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.validation.Valid;

import java.util.Set;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        // Validate if the email already exists
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "error.user", "Email already exists");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        // Fetch the USER role from the database and assign it to the user
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalArgumentException("Role 'USER' not found"));
        user.setRoles(Set.of(userRole)); // Assign the USER role

        // Register the user
        userService.registerUser(user);

        model.addAttribute("message", "User registered successfully!");
        return "login"; // Redirect to login page after registration
    }

}
