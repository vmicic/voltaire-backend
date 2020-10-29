package com.voltaire.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Enumeration;

public class ApiKeyAuthFilter extends AbstractPreAuthenticatedProcessingFilter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String apiKeyHeaderName;

    public ApiKeyAuthFilter(String headerName) {
        this.apiKeyHeaderName = headerName;
    }

    @SneakyThrows
    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();

        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                var headerName = headerNames.nextElement();
                System.out.println(headerName + ": " + request.getHeader(headerName));
            }
        }
        log.info("Api key header value: " + request.getHeader(apiKeyHeaderName));
        return request.getHeader(apiKeyHeaderName);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }

}
