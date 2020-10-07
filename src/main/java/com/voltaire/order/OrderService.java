package com.voltaire.order;

import com.voltaire.exception.customexceptions.BadRequestException;
import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.order.model.*;
import com.voltaire.order.repository.OrderRepository;
import com.voltaire.restaurant.MenuItemService;
import com.voltaire.restaurant.RestaurantService;
import com.voltaire.shared.IdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantService restaurantService;
    private final MenuItemService menuItemService;

    public Order createOrder(OrderDto orderDto) {
        var order = Order.builder()
                .orderStatus(OrderStatus.CREATED)
                .restaurant(restaurantService.findById(orderDto.getRestaurantId()))
                .build();

        createOrderItems(orderDto.getOrderItems(), order);

        return orderRepository.save(order);
    }

    public void createOrderItems(List<OrderItemDto> orderItemDtos, Order order) {
        orderItemDtos.forEach(orderItemDto -> {
            var orderItem = OrderItem.builder()
                    .menuItem(menuItemService.findById(orderItemDto.getMenuItemId()))
                    .order(order)
                    .quantity(orderItemDto.getQuantity())
                    .additionalInfo(orderItemDto.getAdditionalInfo())
                    .build();

            order.addOrderItem(orderItem);
        });
    }

    public IdResponse confirmOrder(Long id) {
        Order order = findById(id);
        if (order.notWaitingForResponse()) {
            throw new BadRequestException("Requested order is not waiting response.");
        }

        order.setOrderStatus(OrderStatus.CONFIRMED);
        return new IdResponse(orderRepository.save(order).getId());
    }

    public IdResponse rejectOrder(Long id) {
        Order order = findById(id);
        if (order.notWaitingForResponse()) {
            throw new BadRequestException("Requested order is not waiting response.");
        }

        order.setOrderStatus(OrderStatus.REJECTED);
        return new IdResponse(orderRepository.save(order).getId());
    }

    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(Order.class, "id", id.toString()));
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}
