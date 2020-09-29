package com.voltaire.order;

import com.voltaire.restaurant.Restaurant;
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


    //TODO get orders from user
}
