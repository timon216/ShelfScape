package com.shelfscape.controller;

import com.shelfscape.model.Loan;
import com.shelfscape.model.LoanStatus;
import com.shelfscape.model.User;
import com.shelfscape.service.LoanService;
import com.shelfscape.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        boolean hasBorrowedBooks = loans.stream().anyMatch(loan -> loan.getStatus() == LoanStatus.BORROWED);

        model.addAttribute("user", user);
        model.addAttribute("loans", loans);
        model.addAttribute("hasBorrowedBooks", hasBorrowedBooks); // Add the hasBorrowedBooks attribute

        return "user/profile";
    }


    @PostMapping("/remove-loan/{loanId}")
    public String removeLoan(@PathVariable Long loanId) {
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = currentUser.getUsername();

        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Loan loan = loanService.removeReservation(loanId, user.getId());

        if (loan != null) {
            return "redirect:/user/profile";
        }

        return "error";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users/{id}/loans")
    public String viewUserLoans(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        List<Loan> loans = loanService.getLoansByUserId(id);

        model.addAttribute("user", user);
        model.addAttribute("loans", loans);

        return "admin/user-loans";
    }

    @GetMapping("/profile-update")
    public String showProfileUpdatePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails currentUser)) {
            return "redirect:/login";
        }

        String email = currentUser.getUsername();
        model.addAttribute("email", email);

        return "user/profile-update";
    }

    @PostMapping("/profile-update")
    public String updateUserProfile(@RequestParam String lastName,
                                    @RequestParam(required = false) String oldPassword,
                                    @RequestParam(required = false) String newPassword,
                                    @RequestParam(required = false) String confirmPassword,
                                    RedirectAttributes redirectAttributes) {
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = currentUser.getUsername();

        // If the user provides an old password and new password, we need to update the password
        if (oldPassword != null && !oldPassword.isEmpty()) {
            boolean isOldPasswordCorrect = userService.checkOldPassword(email, oldPassword);
            if (!isOldPasswordCorrect) {
                redirectAttributes.addFlashAttribute("errorMessage", "Old password is incorrect.");
                return "redirect:/user/profile-update";
            }

            // Check if new password and confirm password match
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "New passwords do not match.");
                return "redirect:/user/profile-update";
            }

            // Update the password
            userService.updatePassword(email, oldPassword, newPassword, confirmPassword);
        }

        // If only the last name is provided, update the last name
        if (lastName != null && !lastName.isEmpty()) {
            userService.updateUserProfile(email, lastName, null); // null for password as it’s not being changed
        }

        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/user/profile";
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping("/delete/{id}")
    public String deleteUserAccount(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUserAccount(id);

            // Clear the security context and invalidate the session
            SecurityContextHolder.clearContext();
            request.getSession().invalidate();

            return "redirect:/login";
        } catch (IllegalStateException e) {
            // If an error occurs, add the error message to RedirectAttributes
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile"; // Redirect to the user's profile page
        }
    }





}
