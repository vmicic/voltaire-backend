package com.voltaire.security;

import com.voltaire.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@Log
@RequiredArgsConstructor
public class UserInfoAuthManager implements AuthenticationManager {

    private final UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) {
        var userEmail = authentication.getName();
        var user = userService.findByEmail(userEmail);

        return new PreAuthenticatedAuthenticationToken(user, userEmail, user.getAuthorities());
    }
}
