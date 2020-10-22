package com.voltaire.security;

import com.voltaire.delivery.ApiKeyService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@AllArgsConstructor
public class ApiKeyAuthManager implements AuthenticationManager {

    private final ApiKeyService apiKeyService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String apiKeyString = (String) authentication.getPrincipal();

        if (apiKeyService.notExists(apiKeyString)) {
            throw new BadCredentialsException("The API key was not found or not the expected value.");
        }

        authentication.setAuthenticated(true);
        return authentication;
    }
}
