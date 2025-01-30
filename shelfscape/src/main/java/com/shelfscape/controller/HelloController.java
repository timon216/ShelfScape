package com.shelfscape.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HelloController {

    @GetMapping("/hello")
    public String helloPage(Principal principal, Model model) {
        // Przekazanie imienia użytkownika do widoku
        model.addAttribute("username", principal.getName());
        return "hello";
    }
}

