package com.shelfscape.controller;

import com.shelfscape.model.Book;
import com.shelfscape.model.Loan;
import com.shelfscape.model.User;
import com.shelfscape.service.BookService;
import com.shelfscape.service.LoanService;
import com.shelfscape.service.UserService;
import com.shelfscape.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            Model model) {

        List<User> users;
        List<Book> books;

        // Search for users
        if (userSearchQuery != null && !userSearchQuery.isEmpty()) {
            users = userService.searchUsers(userSearchQuery); // Add search method in UserService
            model.addAttribute("userSearchQuery", userSearchQuery);
        } else {
            users = userService.getAllUsers(); // Default to show all users
        }

        // Search for books
        if (bookSearchQuery != null && !bookSearchQuery.isEmpty()) {
            books = bookService.searchBooks(bookSearchQuery); // Add search method in BookService
            model.addAttribute("bookSearchQuery", bookSearchQuery);
        } else {
            books = bookService.getAllBooks(); // Default to show all books
        }

        model.addAttribute("users", users);
        model.addAttribute("books", books);
        return "admin/dashboard";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/books/edit/{id}")
    public String editBook(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        model.addAttribute("book", book);
        return "admin/edit-book";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/books/edit/{id}")
    public String updateBook(@PathVariable Long id, @ModelAttribute Book book, @RequestParam(defaultValue = "false") boolean available) {
        Book existingBook = bookService.getBookById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setGenre(book.getGenre());
        existingBook.setIsbn(book.getIsbn());
        existingBook.setAvailable(available);
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
    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
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
        model.addAttribute("users", users);
        return "admin/users";
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
}

