package com.voltaire.delivery;

import com.voltaire.delivery.model.CreateDeliveryCompanyRequest;
import com.voltaire.delivery.model.DeliveryCompany;
import com.voltaire.order.model.OrderForDelivery;
import com.voltaire.shared.IdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/")
public class DeliveryCompanyController {

    private final DeliveryCompanyService deliveryCompanyService;

    @PreAuthorize("hasRole('ROLE_DELIVERY_COMPANY')")
    @PostMapping("/delivery-companies")
    @ResponseStatus(HttpStatus.CREATED)
    public DeliveryCompany createDeliveryCompany(@RequestBody CreateDeliveryCompanyRequest createDeliveryCompanyRequest) {
        return deliveryCompanyService.createDeliveryCompany(createDeliveryCompanyRequest);
    }

    @PreAuthorize("hasRole('ROLE_DELIVERY_COMPANY')")
    @GetMapping("/orders/for-delivery")
    public List<OrderForDelivery> getOrdersForDelivery(@RequestParam(required = false) String address) {
        return deliveryCompanyService.getOrdersForDelivery(address);
    }

    @PreAuthorize("hasRole('ROLE_DELIVERY_COMPANY')")
    @PutMapping("orders/{id}/delivery-request")
    public IdResponse takeOrderToDeliver(@PathVariable UUID id) {
        return deliveryCompanyService.takeOrderToDeliver(id);
    }

    @PreAuthorize("hasRole('ROLE_DELIVERY_COMPANY')")
    @PutMapping("orders/{id}/start-delivery")
    public IdResponse startDelivery(@PathVariable UUID id) {
        return deliveryCompanyService.startDelivery(id);
    }

    @PreAuthorize("hasRole('ROLE_DELIVERY_COMPANY')")
    @PutMapping("orders/{id}/delivered")
    public IdResponse orderDelivered(@PathVariable UUID id) {
        return deliveryCompanyService.orderDelivered(id);
    }

}
