package com.voltaire.order;

import com.voltaire.restaurant.RestaurantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final RestaurantService restaurantService;

    public OrderController(OrderService orderService, RestaurantService restaurantService) {
        this.orderService = orderService;
        this.restaurantService = restaurantService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO orderDTO) {
        if(restaurantService.notExists(orderDTO.getRestaurantId())) {
            return new ResponseEntity<>("Restaurant doesn't exist", HttpStatus.NOT_FOUND);
        }

        Order order = orderService.createOrder(orderDTO);

        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        List<Order> orders = orderService.findAll();

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        if(orderService.notExists(id)) {
            return new ResponseEntity<>("Requested order not found.", HttpStatus.NOT_FOUND);
        }

        Order order = orderService.findById(id);

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<?> confirmOrder(@PathVariable Long id) {
        if(orderService.notExists(id)) {
            return new ResponseEntity<>("Requested order not found.", HttpStatus.NOT_FOUND);
        }

        if(orderService.notWaitingConfirmOrReject(id)) {
            return new ResponseEntity<>("Requested order is not waiting for confirmation.", HttpStatus.BAD_REQUEST);
        }

        orderService.confirmOrder(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectOrder(@PathVariable Long id) {
        if(orderService.notExists(id)) {
            return new ResponseEntity<>("Requested order not found.", HttpStatus.NOT_FOUND);
        }

        if(orderService.notWaitingConfirmOrReject(id)) {
            return new ResponseEntity<>("Requested order is not waiting for rejection.", HttpStatus.BAD_REQUEST);
        }

        orderService.rejectOrder(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
