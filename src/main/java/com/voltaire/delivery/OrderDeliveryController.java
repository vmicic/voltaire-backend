package com.voltaire.delivery;

import com.voltaire.order.model.OrderForDelivery;
import com.voltaire.shared.IdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/orders")
public class OrderDeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/for-delivery")
    public List<OrderForDelivery> getOrdersForDelivery() {
        return deliveryService.getOrdersForDelivery();
    }

    @PutMapping("{id}/deliver")
    public IdResponse takeOrderToDeliver(@PathVariable UUID id) {
        return deliveryService.takeOrderToDeliver(id);
    }

    @PutMapping("{id}/start-delivery")
    public IdResponse startDelivery(@PathVariable UUID id) {
        return deliveryService.startDelivery(id);
    }

    @PutMapping("{id}/delivered")
    public IdResponse orderDelivered(@PathVariable UUID id) {
        return deliveryService.orderDelivered(id);
    }
}
