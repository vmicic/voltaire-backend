package com.voltaire.security;

import com.voltaire.delivery.DeliveryCompanyService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import java.util.UUID;

@AllArgsConstructor
public class ApiKeyAuthManager implements AuthenticationManager {

    private final DeliveryCompanyService deliveryCompanyService;

    @Override
    public Authentication authenticate(Authentication authentication) {
        String apiKeyString = (String) authentication.getPrincipal();

        if (deliveryCompanyService.invalidApiKey(UUID.fromString(apiKeyString))) {
            throw new BadCredentialsException("The API key was not found or not the expected value.");
        }

        authentication.setAuthenticated(true);
        return authentication;
    }
}
