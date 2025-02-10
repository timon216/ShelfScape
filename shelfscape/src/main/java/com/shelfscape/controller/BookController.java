package com.shelfscape.controller;

import com.shelfscape.model.Book;
import com.shelfscape.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public String getAllBooks() {
        return "book-list";
    }

    @GetMapping("/{id}")
    public String getBookById(@PathVariable Long id) {
        return "book-details";
    }

    @PostMapping("/reserve/{id}")
    public String reserveBook(@PathVariable Long id) {
        try {
            bookService.reserveBook(id);
            return "redirect:/catalogue"; // Redirect to the catalogue page after successful reservation
        } catch (Exception e) {
            return "redirect:/catalogue?error=" + e.getMessage(); // Redirect to catalogue with error message if there's an issue
        }
    }

    @PostMapping
    public String addBook(@RequestBody Book book) {
        bookService.saveBook(book);
        return "redirect:/catalogue"; // Redirect to catalogue after adding a book
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "redirect:/catalogue"; // Redirect to catalogue after deleting a book
    }
}
