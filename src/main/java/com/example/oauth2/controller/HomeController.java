package com.example.oauth2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {
    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
                "message", "Welcome to the OAuth2 demo API",
                "login", "/oauth2/authorization/google",
                "profile", "/profile"
        );
    }
}
