package com.voltaire.security;

import com.voltaire.user.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserInfoAuthManager implements AuthenticationManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) {
        var userEmail = authentication.getName();
        var user = userService.findByEmail(userEmail);

        var preAuthenticatedAuthenticationToken = new PreAuthenticatedAuthenticationToken(user, userEmail);
        preAuthenticatedAuthenticationToken.setAuthenticated(true);
        return preAuthenticatedAuthenticationToken;
    }
}
