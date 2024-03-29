package com.voltaire.order;

import com.voltaire.order.model.CreateOrderRequest;
import com.voltaire.order.model.Order;
import com.voltaire.shared.IdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        return orderService.createOrder(createOrderRequest);
    }


    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable UUID id) {
        return orderService.findById(id);
    }

    @PutMapping("/{id}/confirm")
    public IdResponse confirmOrder(@PathVariable UUID id, @RequestBody Integer minutesForPreparation) {
        return orderService.confirmOrder(id, minutesForPreparation);
    }

    @PutMapping("/{id}/reject")
    public IdResponse rejectOrder(@PathVariable UUID id) {
        return orderService.rejectOrder(id);
    }

}
