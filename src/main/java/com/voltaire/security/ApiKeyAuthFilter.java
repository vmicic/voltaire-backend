package com.voltaire.security;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;

public class ApiKeyAuthFilter extends AbstractPreAuthenticatedProcessingFilter {

    private final String apiKeyHeaderName;

    public ApiKeyAuthFilter(String headerName) {
        this.apiKeyHeaderName = headerName;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        System.out.println("checking filter");
        return request.getHeader(apiKeyHeaderName);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }

}
