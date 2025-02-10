package com.shelfscape.controller;

import com.shelfscape.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/dashboard";
    }
}
