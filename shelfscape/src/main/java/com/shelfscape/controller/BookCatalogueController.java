package com.shelfscape.controller;

import com.shelfscape.model.Book;
import com.shelfscape.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/catalogue")
public class BookCatalogueController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public String viewCatalogue(Model model, Authentication authentication) {
        List<Book> books = bookService.getAllBooks();
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated();

        model.addAttribute("books", books);
        model.addAttribute("isLoggedIn", isLoggedIn);

        return "catalogue";
    }
}
