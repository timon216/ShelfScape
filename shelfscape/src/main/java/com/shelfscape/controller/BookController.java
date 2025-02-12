package com.shelfscape.controller;

import com.shelfscape.model.Book;
import com.shelfscape.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String reserveBook(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            bookService.reserveBook(id);
            return "redirect:/catalogue";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/catalogue";
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
