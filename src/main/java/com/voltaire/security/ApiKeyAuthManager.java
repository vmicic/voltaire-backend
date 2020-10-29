package com.voltaire.security;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

@AllArgsConstructor
public class ApiKeyAuthManager implements AuthenticationManager {

    private final SecretManagerService secretManagerService;

    @Override
    public Authentication authenticate(Authentication authentication) {
        String apiKey = (String) authentication.getPrincipal();

        if (secretManagerService.invalidApiKey(apiKey)) {
            throw new BadCredentialsException("The API key was not found or not the expected value.");
        }

        authentication.setAuthenticated(true);
        return authentication;
    }
}
