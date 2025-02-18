package com.shelfscape.controller;

import com.shelfscape.model.Book;
import com.shelfscape.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/catalogue")
public class BookCatalogueController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public String viewCatalogue(@RequestParam(value = "search", required = false) String searchQuery,
                                @RequestParam(value = "error", required = false) String error,
                                Model model,
                                Authentication authentication) {

        List<Book> books;

        // If search query is provided, filter books
        if (searchQuery != null && !searchQuery.isEmpty()) {
            books = bookService.searchBooks(searchQuery);
            model.addAttribute("searchQuery", searchQuery);
        } else {
            books = bookService.getAllBooks();
        }

        boolean isLoggedIn = authentication != null && authentication.isAuthenticated();

        model.addAttribute("books", books);
        model.addAttribute("isLoggedIn", isLoggedIn);

        if (error != null) {
            model.addAttribute("error", error);
        }

        return "catalogue";
    }
}
