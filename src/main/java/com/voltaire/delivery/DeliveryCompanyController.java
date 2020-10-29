package com.voltaire.delivery;

import com.voltaire.delivery.model.CreateDeliveryCompanyRequest;
import com.voltaire.delivery.model.DeliveryCompany;
import com.voltaire.order.model.OrderForDelivery;
import com.voltaire.shared.IdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/")
public class DeliveryCompanyController {

    private final DeliveryCompanyService deliveryCompanyService;

    @PostMapping("/delivery-companies")
    @ResponseStatus(HttpStatus.CREATED)
    public DeliveryCompany createDeliveryCompany(@RequestBody CreateDeliveryCompanyRequest createDeliveryCompanyRequest) {
        return deliveryCompanyService.createDeliveryCompany(createDeliveryCompanyRequest);
    }

    @GetMapping("/orders/for-delivery")
    public List<OrderForDelivery> getOrdersForDelivery(@RequestParam(required = false) String address) {
        System.out.println("get orders for delivery");
        return deliveryCompanyService.getOrdersForDelivery(address);
    }

    @PutMapping("orders/{id}/delivery-request")
    public IdResponse takeOrderToDeliver(@PathVariable UUID id) {
        return deliveryCompanyService.takeOrderToDeliver(id);
    }

    @PutMapping("orders/{id}/start-delivery")
    public IdResponse startDelivery(@PathVariable UUID id) {
        return deliveryCompanyService.startDelivery(id);
    }

    @PutMapping("orders/{id}/delivered")
    public IdResponse orderDelivered(@PathVariable UUID id) {
        return deliveryCompanyService.orderDelivered(id);
    }

}
