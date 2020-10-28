package com.voltaire.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${voltaire.http.api-key-header-name}")
    private String apiKeyHeaderName;

    private final SecretManagerService secretManagerService;

    private static final String[] DELIVERY_SERVICE_URLS = {
            "/v1/orders/for-delivery",
            "/v1/orders/**/deliver",
            "/v1/orders/**/start-delivery",
            "/v1/orders/**/delivered"
    };

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        ApiKeyAuthFilter filter = new ApiKeyAuthFilter(apiKeyHeaderName);
        filter.setAuthenticationManager(new ApiKeyAuthManager(secretManagerService));

        httpSecurity
                .headers().frameOptions().disable()
                .and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilter(filter).
                authorizeRequests().antMatchers(DELIVERY_SERVICE_URLS).authenticated();
    }

}
