package com.ecommerce.ecommerce_from.jee.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthViewController {

    @GetMapping("/login.html")
    public String showLoginForm() {
        return "login"; // templates/login.html
    }

    @GetMapping("/register.html")
    public String showRegisterForm() {
        return "register"; // templates/register.html
    }

    @GetMapping("/")
    public String index() {
        return "index"; // templates/index.html
    }
}
