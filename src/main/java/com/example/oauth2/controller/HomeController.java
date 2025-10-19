package com.example.oauth2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "home"; // needs src/main/resources/templates/home.html if using Thymeleaf
    }
}
