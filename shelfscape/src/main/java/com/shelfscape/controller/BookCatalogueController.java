package com.shelfscape.controller;

import com.shelfscape.model.Book;
import com.shelfscape.service.BookService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/catalogue")
public class BookCatalogueController {

    private final BookService bookService;

    public BookCatalogueController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public String showCatalogue(@RequestParam(required = false) String search,
                                @RequestParam(required = false) List<String> genres,
                                Model model) {
        List<Book> books = bookService.searchAndFilterByGenres(search, genres);
        List<String> allGenres = bookService.getAllGenres();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated() &&
                !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"));

        model.addAttribute("books", books);
        model.addAttribute("searchQuery", search);
        model.addAttribute("allGenres", allGenres);
        model.addAttribute("genres", genres);
        model.addAttribute("isLoggedIn", isLoggedIn); // Add this attribute

        return "catalogue";
    }
}
