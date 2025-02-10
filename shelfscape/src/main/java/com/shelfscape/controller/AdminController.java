package com.shelfscape.controller;

import com.shelfscape.model.User;
import com.shelfscape.service.UserService;
import com.shelfscape.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/dashboard")
    public String adminDashboard(@RequestParam(value = "search", required = false) String searchQuery, Model model) {
        List<User> users;

        if (searchQuery != null && !searchQuery.isEmpty()) {
            users = userService.searchUsers(searchQuery); // Add search method in UserService
            model.addAttribute("searchQuery", searchQuery);
        } else {
            users = userService.getAllUsers(); // Default to show all users
        }

        model.addAttribute("users", users);
        return "admin/dashboard";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.getAllRoles()); // Add roles to the model for dropdown
        return "admin/edit-user";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/users/edit/{id}")
    public String updateUser(@PathVariable Long id, User user) {
        User existingUser = userService.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setRoles(user.getRoles()); // Update roles if necessary
        userService.save(existingUser);
        return "redirect:/admin/dashboard";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users")
    public String viewAllUsers(@RequestParam(value = "search", required = false) String searchQuery, Model model) {
        List<User> users;

        if (searchQuery != null && !searchQuery.isEmpty()) {
            users = userService.searchUsers(searchQuery);
            model.addAttribute("searchQuery", searchQuery);
        } else {
            users = userService.getAllUsers();
        }

        model.addAttribute("users", users);
        return "admin/users";
    }

}
