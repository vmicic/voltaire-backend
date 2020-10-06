package com.voltaire.exceptions.customexceptions;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Class<?> clazz, String... searchParamsMap) {
        super(EntityNotFoundException.generateMessage(clazz.getSimpleName(), toMap(searchParamsMap)));
    }

    private static String generateMessage(String entity, Map<String, String> searchParams) {
        return StringUtils.capitalize(entity) +
                " was not found for parameters " +
                searchParams;
    }

    private static Map<String, String> toMap(String... searchParams) {
        return IntStream.range(0, searchParams.length)
                .filter(i -> i % 2 == 0)
                .collect(HashMap::new,
                        (m, i) -> m.put(searchParams[i], searchParams[i + 1]),
                        Map::putAll);
    }
}
