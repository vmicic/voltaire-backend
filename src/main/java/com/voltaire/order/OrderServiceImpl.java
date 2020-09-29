package com.voltaire.order;

import com.voltaire.restaurant.MenuItemService;
import com.voltaire.restaurant.Restaurant;
import com.voltaire.restaurant.RestaurantService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantService restaurantService;
    private final OrderItemService orderItemService;

    public OrderServiceImpl(OrderRepository orderRepository, RestaurantService restaurantService, MenuItemService menuItemService, OrderItemService orderItemService) {
        this.orderRepository = orderRepository;
        this.restaurantService = restaurantService;
        this.orderItemService = orderItemService;
    }

    @Override
    public Order createOrder(OrderDTO orderDTO) {
        Order order = new Order();

        Restaurant restaurant = restaurantService.findById(orderDTO.getRestaurantId());
        order.setRestaurant(restaurant);
        order.setLocalDateTime(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.CREATED);

        Order newOrder = orderRepository.save(order);

        List<OrderItem> orderItems = orderItemService.saveAll(newOrder, orderDTO.getOrderItems());
        newOrder.setOrderItems(orderItems);

        return newOrder;
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public boolean notExists(Long id) {
        return !orderRepository.existsById(id);
    }


}
