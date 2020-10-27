package com.voltaire.security;

import com.voltaire.delivery.DeliveryCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("v1/api-keys")
public class ApiKeyController {

    private final DeliveryCompanyService deliveryCompanyService;

    @GetMapping
    public UUID createApiKey(@RequestParam UUID deliveryCompanyId) {
        return deliveryCompanyService.getApiKey(deliveryCompanyId);
    }

    @PostMapping
    public UUID generateNewApiKey(@RequestParam UUID deliveryCompanyId) {
        return deliveryCompanyService.generateNewApiKey(deliveryCompanyId);
    }
}
