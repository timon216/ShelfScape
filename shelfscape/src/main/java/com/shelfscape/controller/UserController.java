package com.shelfscape.controller;

import com.shelfscape.model.Loan;
import com.shelfscape.model.User;
import com.shelfscape.service.LoanService;
import com.shelfscape.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final LoanService loanService;

    @Autowired
    public UserController(UserService userService, LoanService loanService) {
        this.userService = userService;
        this.loanService = loanService;
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        return userService.authenticate(user);
    }

    @GetMapping("/profile")
    public String userProfile(Model model) {
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = currentUser.getUsername();

        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        List<Loan> loans = loanService.getLoansByUser(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("loans", loans);

        return "user/profile";
    }
}
