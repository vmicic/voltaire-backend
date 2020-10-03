package com.voltaire.order;

import com.voltaire.order.model.*;
import com.voltaire.order.repository.OrderRepository;
import com.voltaire.restaurant.MenuItemService;
import com.voltaire.restaurant.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantService restaurantService;
    private final MenuItemService menuItemService;

    public Order createOrder(OrderDto orderDto) {
        var order = Order.builder()
                .orderTime(LocalDateTime.now())
                .orderStatus(OrderStatus.CREATED)
                .restaurant(restaurantService.findById(orderDto.getRestaurantId()))
                .build();
        createOrderItems(orderDto.getOrderItems(), order);

        return orderRepository.save(order);
    }

    public void createOrderItems(List<OrderItemDto> orderItemDtos, Order order) {
        orderItemDtos.forEach(orderItemDto -> {
            var orderItem =  OrderItem.builder()
                    .menuItem(menuItemService.findById(orderItemDto.getMenuItemId()))
                    .order(order)
                    .quantity(orderItemDto.getQuantity())
                    .additionalInfo(orderItemDto.getAdditionalInfo())
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
