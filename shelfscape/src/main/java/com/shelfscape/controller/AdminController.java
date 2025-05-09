package com.shelfscape.controller;

import com.shelfscape.model.Book;
import com.shelfscape.model.Loan;
import com.shelfscape.model.LoanStatus;
import com.shelfscape.model.User;
import com.shelfscape.service.BookService;
import com.shelfscape.service.LoanService;
import com.shelfscape.service.UserService;
import com.shelfscape.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private BookService bookService;

    @Autowired
    private LoanService loanService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/dashboard")
    public String adminDashboard(
            @RequestParam(value = "userSearch", required = false) String userSearchQuery,
            @RequestParam(value = "bookSearch", required = false) String bookSearchQuery,
            @RequestParam(value = "loanSearch", required = false) String loanSearchQuery,
            Model model) {

        List<User> users;
        List<Book> books;
        List<Loan> loans;

        if (userSearchQuery != null && !userSearchQuery.isEmpty()) {
            users = userService.searchUsers(userSearchQuery);
            model.addAttribute("userSearchQuery", userSearchQuery);
        } else {
            users = userService.getAllUsers();
        }

        if (bookSearchQuery != null && !bookSearchQuery.isEmpty()) {
            books = bookService.searchBooks(bookSearchQuery);
            model.addAttribute("bookSearchQuery", bookSearchQuery);
        } else {
            books = bookService.getAllBooks();
        }

        if (loanSearchQuery != null && !loanSearchQuery.isEmpty()) {
            loans = loanService.searchLoans(loanSearchQuery);
            model.addAttribute("loanSearchQuery", loanSearchQuery);
        } else {
            loans = loanService.getAllLoans();
        }

        model.addAttribute("users", users);
        model.addAttribute("books", books);
        model.addAttribute("loans", loans);
        return "admin/dashboard";
    }

    // Book-related methods
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/books/edit/{id}")
    public String editBook(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        model.addAttribute("book", book);
        return "admin/edit-book";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/books/edit/{id}")
    public String updateBook(@PathVariable Long id, @ModelAttribute Book book, @RequestParam int quantity) {
        Book existingBook = bookService.getBookById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setGenre(book.getGenre());
        existingBook.setIsbn(book.getIsbn());
        existingBook.setQuantity(quantity); // Update the quantity
        bookService.saveBook(existingBook);
        return "redirect:/admin/books";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/books/add")
    public String addBook(Model model) {
        model.addAttribute("book", new Book());
        return "admin/add-book";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/books/add")
    public String saveBook(Book book) {
        bookService.saveBook(book);
        return "redirect:/admin/books";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/books/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "redirect:/admin/books";
    }

    // User-related methods
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
        existingUser.setRoles(user.getRoles());
        userService.save(existingUser);
        return "redirect:/admin/dashboard";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserByAdmin(id);
        return "redirect:/admin/users";
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

        // Check if each user has active loans
        for (User user : users) {
            boolean hasActiveLoans = loanService.getLoansByUser(user.getId()).stream()
                    .anyMatch(loan -> loan.getStatus() == LoanStatus.BORROWED);
            user.setHasActiveLoans(hasActiveLoans); // Assuming you have a `hasActiveLoans` field in the User model
        }

        model.addAttribute("users", users);
        return "admin/users"; // Assuming your template is named "users.html"
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/books")
    public String viewAllBooks(@RequestParam(value = "search", required = false) String searchQuery, Model model) {
        List<Book> books;
        if (searchQuery != null && !searchQuery.isEmpty()) {
            books = bookService.searchBooks(searchQuery);
            model.addAttribute("searchQuery", searchQuery);
        } else {
            books = bookService.getAllBooks();
        }
        model.addAttribute("books", books);
        return "admin/books";
    }

    // loans-related methods
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/loans")
    public String viewAllLoans(@RequestParam(value = "search", required = false) String searchQuery, Model model) {
        List<Loan> loans;
        if (searchQuery != null && !searchQuery.isEmpty()) {
            loans = loanService.searchLoans(searchQuery);
            model.addAttribute("searchQuery", searchQuery);
        } else {
            loans = loanService.getAllLoans();
        }
        model.addAttribute("loans", loans);
        return "admin/loans";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/loans/{id}")
    public Optional<Loan> getLoanById(@PathVariable Long id) {
        return loanService.getLoanById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/loans/{id}/status")
    public String updateLoanStatus(@PathVariable Long id, @RequestParam LoanStatus status, Model model) {
        Optional<Loan> optionalLoan = loanService.getLoanById(id);
        if (optionalLoan.isPresent()) {
            Loan loan = optionalLoan.get();
            loan.setStatus(status);
            loanService.saveLoan(loan);
        }
        return "redirect:/admin/loans";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/loans/overdue")
    public List<Loan> getOverdueLoans() {
        List<Loan> loans = loanService.getAllLoans();
        return loans.stream()
                .filter(loan -> loan.getStatus() == LoanStatus.OVERDUE)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/loans/updateStatus")
    public String updateLoanStatus(@RequestParam Long loanId, @RequestParam LoanStatus status, @RequestParam String redirectUrl) {
        loanService.updateLoanStatus(loanId, status);
        return "redirect:" + redirectUrl;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/loans/extend")
    public String extendLoan(@RequestParam Long loanId, @RequestParam String redirectUrl) {
        loanService.extendLoan(loanId);
        return "redirect:" + redirectUrl;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/loans/delete/{id}")
    public String deleteLoan(@PathVariable Long id, @RequestParam String redirectUrl) {
        Loan loan = loanService.getLoanById(id).orElseThrow(() -> new RuntimeException("Loan not found"));

        // Make the book available again if the loan was either BORROWED or RESERVED
        if (loan.getStatus() == LoanStatus.BORROWED || loan.getStatus() == LoanStatus.RESERVED) {
            Book book = loan.getBook();
            book.setQuantity(book.getQuantity() + 1); // Increment quantity
            bookService.saveBook(book);
        }

        loanService.deleteLoan(id);
        return "redirect:" + redirectUrl;
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
}
