package com.voltaire.delivery;

import com.voltaire.delivery.model.ApiKey;
import com.voltaire.delivery.repository.ApiKeyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKey createApiKey() {
        return apiKeyRepository.save(new ApiKey());
    }

    public boolean notExists(String apiKey) {
        return !apiKeyRepository.existsById(UUID.fromString(apiKey));
    }
}
