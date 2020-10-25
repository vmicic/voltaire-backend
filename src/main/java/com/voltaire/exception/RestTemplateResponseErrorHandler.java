package com.voltaire.exception;

import com.voltaire.exception.customexceptions.BadRequestException;
import com.voltaire.exception.customexceptions.InternalServerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

@Component
public class RestTemplateResponseErrorHandler extends DefaultResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        if (httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
            throw new InternalServerException("Internal server error");
        }

        if (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
            throw new BadRequestException("Bad request");
        }
    }
}
