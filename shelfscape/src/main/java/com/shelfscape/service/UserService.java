package com.shelfscape.service;

import com.shelfscape.model.*;
import com.shelfscape.repository.BookRepository;
import com.shelfscape.repository.LoanRepository;
import com.shelfscape.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    @Autowired
    private LoanService loanService;

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
        return userRepository.findByEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searchQuery, searchQuery, searchQuery);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean canReserveOrBorrow(User user) {
        long reservedBooksCount = loanService.countLoansByUserAndStatus(user, LoanStatus.RESERVED);
        long borrowedBooksCount = loanService.countLoansByUserAndStatus(user, LoanStatus.BORROWED);

        return (reservedBooksCount + borrowedBooksCount) < 6;
    }

    public void updateUserProfile(String email, String lastName, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update last name if provided
        if (lastName != null && !lastName.isEmpty()) {
            user.setLastName(lastName);
        }

        // Update password if provided
        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        userRepository.save(user);
    }

    public boolean checkOldPassword(String email, String oldPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    public void updatePassword(String email, String oldPassword, String newPassword, String confirmPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Check if the old password is correct
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect.");
        }

        // Check if the new password and confirm password match
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("New password and confirmation do not match.");
        }

        // Update the password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deleteUserAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found with ID: " + userId));

        boolean hasActiveLoans = user.getLoans().stream()
                .anyMatch(loan -> loan.getStatus() == LoanStatus.BORROWED);

        if (hasActiveLoans) {
            throw new IllegalStateException("You have active loans and cannot delete the account. You need to return the borrowed books first.");
        }

        // Detach loans from the user
        List<Loan> loans = new ArrayList<>(user.getLoans());
        user.getLoans().clear();

        // Delete reserved loans and update book quantities
        for (Loan loan : loans) {
            if (loan.getStatus() == LoanStatus.RESERVED) {
                Book book = loan.getBook();
                book.setQuantity(book.getQuantity() + 1); // Increment quantity
                bookRepository.save(book);
            }
            loanRepository.delete(loan);
        }

        // Delete the user
        userRepository.delete(user);
    }



    public boolean hasBorrowedBooks(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found with ID: " + userId));

        return user.getLoans().stream()
                .anyMatch(loan -> loan.getStatus() == LoanStatus.BORROWED);
    }
}
