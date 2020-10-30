package com.voltaire.security;

import com.voltaire.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${voltaire.http.user-info-header-name}")
    private String userInfoHeaderName;

    private final UserInfoAuthManager userInfoAuthManager;

    private final String[] USER_API = {
            "/v1/restaurants"
    };

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        var userInfoAuthFilter = new UserInfoAuthFilter(userInfoHeaderName);
        userInfoAuthFilter.setAuthenticationManager(userInfoAuthManager);

        httpSecurity
                .headers().frameOptions().disable()
                .and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilter(userInfoAuthFilter)
                .authorizeRequests().antMatchers(USER_API).authenticated();
    }
}

