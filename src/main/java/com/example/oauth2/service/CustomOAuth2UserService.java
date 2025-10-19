package com.example.oauth2.service;

import com.example.oauth2.entity.AuthProvider;
import com.example.oauth2.entity.User;
import com.example.oauth2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User delegateUser = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        Map<String, Object> attributes = delegateUser.getAttributes();
        AuthProvider provider = mapProvider(registrationId);

        // Extract provider-specific IDs and common attributes
        String providerId = extractProviderId(provider, attributes);
        String name = extractName(provider, attributes);
        String email = extractEmail(provider, attributes);

        upsertUser(provider, providerId, name, email);

        Collection<? extends GrantedAuthority> authorities = delegateUser.getAuthorities();
        return new DefaultOAuth2User(authorities, attributes, userNameAttributeName);
    }

    private AuthProvider mapProvider(String registrationId) {
        if (registrationId == null) {
            throw new IllegalArgumentException("registrationId is null");
        }
        switch (registrationId.toLowerCase()) {
            case "google":
                return AuthProvider.GOOGLE;
            case "github":
                return AuthProvider.GITHUB;
            default:
                throw new IllegalArgumentException("Unsupported provider: " + registrationId);
        }
    }

    private String extractProviderId(AuthProvider provider, Map<String, Object> attributes) {
        Object id;
        if (provider == AuthProvider.GOOGLE) {
            id = attributes.get("sub");
        } else if (provider == AuthProvider.GITHUB) {
            id = attributes.get("id");
        } else {
            id = attributes.get("id");
        }
        return id == null ? null : String.valueOf(id);
    }

    private String extractName(AuthProvider provider, Map<String, Object> attributes) {
        Object value = attributes.get("name");
        if (value == null && provider == AuthProvider.GOOGLE) {
            value = attributes.get("given_name");
        }
        if (value == null && provider == AuthProvider.GITHUB) {
            value = attributes.get("login");
        }
        return value == null ? null : String.valueOf(value);
    }

    private String extractEmail(AuthProvider provider, Map<String, Object> attributes) {
        Object value = attributes.get("email");
        if (value == null && provider == AuthProvider.GITHUB) {
            // Some GitHub accounts hide their email; leave as null
            // Alternate approaches could query the /emails endpoint with proper scopes
        }
        return value == null ? null : String.valueOf(value);
    }

    private void upsertUser(AuthProvider provider, String providerId, String name, String email) {
        if (providerId == null) {
            // Without a provider id we cannot key the account; bail out
            return;
        }
        Optional<User> existingByProvider = userRepository.findByProviderAndProviderId(provider, providerId);
        if (existingByProvider.isPresent()) {
            User user = existingByProvider.get();
            boolean changed = false;
            if (name != null && !name.equals(user.getName())) {
                user.setName(name);
                changed = true;
            }
            if (email != null && !email.equals(user.getEmail())) {
                user.setEmail(email);
                changed = true;
            }
            if (changed) {
                userRepository.save(user);
            }
            return;
        }

        // Fallback: try email match to avoid duplicates if possible
        if (email != null) {
            Optional<User> existingByEmail = userRepository.findByEmail(email);
            if (existingByEmail.isPresent()) {
                User user = existingByEmail.get();
                user.setProvider(provider);
                user.setProviderId(providerId);
                if (name != null) {
                    user.setName(name);
                }
                userRepository.save(user);
                return;
            }
        }

        User newUser = User.builder()
                .name(name)
                .email(email)
                .provider(provider)
                .providerId(providerId)
                .build();
        userRepository.save(newUser);
    }
}
