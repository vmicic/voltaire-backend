package com.voltaire.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
@RequiredArgsConstructor
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${voltaire.http.user-info-header-name}")
    private String userInfoHeaderName;

    private final UserInfoAuthManager userInfoAuthManager;

    private static final String[] AUTH_WHITELIST = {
            "/v1/users/**",
            "/v1/orders/for-delivery",
            "/v1/orders/**/delivery-request",
            "/v1/orders/**/start-delivery",
            "/v1/orders/**/delivered"
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
                .authorizeRequests().antMatchers("/v1/**").authenticated();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(AUTH_WHITELIST);
    }
}

