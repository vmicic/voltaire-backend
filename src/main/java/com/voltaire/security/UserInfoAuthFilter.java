package com.voltaire.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

public class UserInfoAuthFilter extends AbstractPreAuthenticatedProcessingFilter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String userInfoHeaderName;


    public UserInfoAuthFilter(String userInfoHeaderName) {
        this.userInfoHeaderName = userInfoHeaderName;
    }


    @SneakyThrows
    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        var userInfoHeader = request.getHeader(userInfoHeaderName);

        if (userInfoHeader == null) {
            log.info("Header from gateway is null");
            return null;
        }

        byte[] decodedBytes = Base64.getDecoder().decode(userInfoHeader);
        var decodedString = new String(decodedBytes);

        var objectMapper = new ObjectMapper();
        var jsonNode = objectMapper.readTree(decodedString);
        return jsonNode.get("email").asText();
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest httpServletRequest) {
        return "N/A";
    }
}
