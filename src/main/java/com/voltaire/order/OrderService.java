package com.voltaire.order;

import com.voltaire.error.customerrors.BadRequestException;
import com.voltaire.error.customerrors.EntityNotFoundException;
import com.voltaire.order.model.*;
import com.voltaire.order.repository.OrderRepository;
import com.voltaire.restaurant.MenuItemService;
import com.voltaire.restaurant.RestaurantService;
import com.voltaire.restaurant.model.MenuItem;
import com.voltaire.restaurant.model.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantService restaurantService;
    private final MenuItemService menuItemService;

    public Order createOrder(OrderDto orderDto) {
        if(restaurantService.notExists(orderDto.getRestaurantId())) {
            throw new EntityNotFoundException(Restaurant.class, "id", orderDto.getRestaurantId().toString());
        }

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
            if(menuItemService.notExists(orderItemDto.getMenuItemId())) {
                throw new EntityNotFoundException(MenuItem.class, "id", orderItemDto.getMenuItemId().toString());
            }

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
        Optional<Order> order = orderRepository.findById(id);
        if(order.isEmpty()) {
            throw new EntityNotFoundException(Order.class, "id", id.toString());
        }

        return order.get();
    }

    public boolean notExists(Long id) {
        return !orderRepository.existsById(id);
    }

    public boolean notWaitingResponse(Long id) {
        Order order = this.findById(id);

        return !order.getOrderStatus().equals(OrderStatus.CREATED);
    }

    public void confirmOrder(Long id) {
        Order order = this.findById(id);
        if(notWaitingResponse(id)) {
            throw new BadRequestException("Request order is not waiting response.");
        }

        order.setOrderStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
    }

    public void rejectOrder(Long id) {
        Order order = this.findById(id);
        if(notWaitingResponse(id)) {
            throw new BadRequestException("Request order is not waiting response.");
        }

        order.setOrderStatus(OrderStatus.REJECTED);
        orderRepository.save(order);
    }
}
