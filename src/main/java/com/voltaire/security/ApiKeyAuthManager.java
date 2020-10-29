package com.voltaire.security;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

@AllArgsConstructor
public class ApiKeyAuthManager implements AuthenticationManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final SecretManagerService secretManagerService;

    @Override
    public Authentication authenticate(Authentication authentication) {
        String apiKey = (String) authentication.getPrincipal();

        log.info("Api key is: " + apiKey);
        log.info("Api key is invalid: " + secretManagerService.invalidApiKey(apiKey));

        if (secretManagerService.invalidApiKey(apiKey)) {
            throw new BadCredentialsException("The API key was not found or not the expected value.");
        }

        authentication.setAuthenticated(true);
        return authentication;
    }
}
