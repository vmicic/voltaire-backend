package com.voltaire.order;

import com.voltaire.order.model.CreateOrderRequest;
import com.voltaire.order.model.Order;
import com.voltaire.shared.IdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/orders")
public class OrderController {

    private final OrderService orderService;


    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        return orderService.createOrder(createOrderRequest);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.findAll();
    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable UUID id) {
        return orderService.findById(id);
    }


    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER')")
    @PutMapping("/{id}/confirm")
    public IdResponse confirmOrder(@PathVariable UUID id, @RequestBody Integer minutesForPreparation) {
        return orderService.confirmOrder(id, minutesForPreparation);
    }

    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER')")
    @PutMapping("/{id}/reject")
    public IdResponse rejectOrder(@PathVariable UUID id) {
        return orderService.rejectOrder(id);
    }

}
