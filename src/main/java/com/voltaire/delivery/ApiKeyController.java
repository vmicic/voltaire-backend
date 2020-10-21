package com.voltaire.delivery;

import com.voltaire.delivery.model.ApiKey;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("v1/api-keys")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    public ApiKey createApiKey() {
        return apiKeyService.createApiKey();
    }
}
