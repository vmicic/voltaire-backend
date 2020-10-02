package com.voltaire.order;

import com.voltaire.order.model.*;
import com.voltaire.order.model.Order.OrderBuilder;
import com.voltaire.order.model.OrderItem.OrderItemBuilder;
import com.voltaire.restaurant.MenuItemService;
import com.voltaire.restaurant.RestaurantService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantService restaurantService;
    private final MenuItemService menuItemService;

    public OrderService(OrderRepository orderRepository, RestaurantService restaurantService, MenuItemService menuItemService) {
        this.orderRepository = orderRepository;
        this.restaurantService = restaurantService;
        this.menuItemService = menuItemService;
    }

    public Order createOrder(OrderDto orderDTO) {
        Order order = new OrderBuilder()
                .setOrderTime(LocalDateTime.now())
                .setOrderStatus(OrderStatus.CREATED)
                .setRestaurant(restaurantService.findById(orderDTO.getRestaurantId()))
                .build();
        createOrderItems(orderDTO.getOrderItems(), order);

        return orderRepository.save(order);
    }

    public void createOrderItems(List<OrderItemDto> orderItemDtos, Order order) {
        orderItemDtos.forEach(orderItemDto -> {
            OrderItem orderItem = new OrderItemBuilder()
                    .setMenuItem(menuItemService.findById(orderItemDto.getMenuItemId()))
                    .setOrder(order)
                    .setQuantity(orderItemDto.getQuantity())
                    .setAdditionalInfo(orderItemDto.getAdditionalInfo())
                    .build();

            order.addOrderItem(orderItem);
        });
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public boolean notExists(Long id) {
        return !orderRepository.existsById(id);
    }

    public boolean notWaitingConfirmOrReject(Long id) {
        Order order = this.findById(id);

        return !order.getOrderStatus().equals(OrderStatus.CREATED);
    }

    public void confirmOrder(Long id) {
        Order order = this.findById(id);

        order.setOrderStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
    }

    public void rejectOrder(Long id) {
        Order order = this.findById(id);

        order.setOrderStatus(OrderStatus.REJECTED);
        orderRepository.save(order);
    }
}
