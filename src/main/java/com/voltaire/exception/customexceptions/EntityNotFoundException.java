package com.voltaire.exception.customexceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Class<?> clazz, String property, String value) {
        super(clazz.getSimpleName() + " was not found for " + property + "=" + value);
    }
}
