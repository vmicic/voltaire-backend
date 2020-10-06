package com.voltaire.order;

import com.voltaire.order.model.Order;
import com.voltaire.order.model.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderDto orderDto) {
        Order order = orderService.createOrder(orderDto);

        return new ResponseEntity<>(order, HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.findAll();

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.findById(id);

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<Long> confirmOrder(@PathVariable Long id) {
        Long orderId = orderService.confirmOrder(id);

        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Long> rejectOrder(@PathVariable Long id) {
        Long orderId = orderService.rejectOrder(id);

        return new ResponseEntity<>(orderId, HttpStatus.NO_CONTENT);
    }
}
