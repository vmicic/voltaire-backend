package com.voltaire.delivery;

import com.voltaire.delivery.model.CreateDeliveryCompanyRequest;
import com.voltaire.order.model.OrderForDeliveryRequest;
import com.voltaire.shared.IdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/")
public class DeliveryCompanyController {

    private final DeliveryCompanyService deliveryCompanyService;

    @PostMapping("/delivery-companies")
    @ResponseStatus(HttpStatus.CREATED)
    public void createDeliveryCompany(@RequestBody CreateDeliveryCompanyRequest createDeliveryCompanyRequest) {
        deliveryCompanyService.createDeliveryCompany(createDeliveryCompanyRequest);
    }

    @GetMapping("/orders/for-delivery")
    public List<OrderForDeliveryRequest> getOrdersForDelivery(@RequestParam(required = false) String address) {
        return deliveryCompanyService.getOrdersForDelivery(address);
    }

    @PutMapping("orders/{id}/delivery-request")
    public IdResponse takeOrderToDeliver(@PathVariable String id) {
        return deliveryCompanyService.takeOrderToDeliver(id);
    }

    @PutMapping("orders/{id}/start-delivery")
    public IdResponse startDelivery(@PathVariable String id) {
        return deliveryCompanyService.startDelivery(id);
    }

    @PutMapping("orders/{id}/delivered")
    public IdResponse orderDelivered(@PathVariable String id) {
        return deliveryCompanyService.orderDelivered(id);
    }

}
