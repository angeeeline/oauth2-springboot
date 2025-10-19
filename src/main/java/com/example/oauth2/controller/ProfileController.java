package com.example.oauth2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController {

    @GetMapping("/profile")
    public String profile() {
        return "Welcome! You are logged in via OAuth2.";
    }
}
