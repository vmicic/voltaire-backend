package com.voltaire.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

public class ApiKeyAuthFilter extends AbstractPreAuthenticatedProcessingFilter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String apiKeyHeaderName;

    public ApiKeyAuthFilter(String headerName) {
        this.apiKeyHeaderName = headerName;
    }

    @SneakyThrows
    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        var headerValue = request.getHeader("X-Apigateway-Api-Userinfo");
        log.info("This is header value: " + headerValue);
        if(headerValue != null) {
            byte[] decodedBytes = Base64.getDecoder().decode(headerValue);
            String decodedString = new String(decodedBytes);
            log.info("Decoded string: " + decodedString);

            ObjectMapper objectMapper = new ObjectMapper();
            var json = objectMapper.readTree(decodedString);
            var emailNode = json.get("email");
            var emailString = String.valueOf(emailNode);
            log.info("This is email from json: " + emailString);
        }
        return request.getHeader(apiKeyHeaderName);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }

}
