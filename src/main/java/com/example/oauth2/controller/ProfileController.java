package com.example.oauth2.controller;

import com.example.oauth2.dto.ProfileDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController {

    @GetMapping("/profile")
    public ProfileDto profile(@AuthenticationPrincipal OAuth2User oauth2User) {
        ProfileDto dto = new ProfileDto();
        if (oauth2User != null) {
            Object name = oauth2User.getAttributes().get("name");
            Object email = oauth2User.getAttributes().get("email");
            if (name != null) dto.setName(String.valueOf(name));
            if (email != null) dto.setEmail(String.valueOf(email));
        }
        return dto;
    }
}
