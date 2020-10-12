package com.voltaire.order;

import com.voltaire.exception.customexceptions.BadRequestException;
import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.order.model.*;
import com.voltaire.order.repository.OrderRepository;
import com.voltaire.restaurant.model.MenuItem;
import com.voltaire.restaurant.model.Restaurant;
import com.voltaire.restaurant.repository.MenuItemRepository;
import com.voltaire.restaurant.repository.RestaurantRepository;
import com.voltaire.shared.IdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    public Order createOrder(OrderDto orderDto) {
        var order = Order.builder()
                .orderStatus(OrderStatus.CREATED)
                .restaurant(findRestaurantById(orderDto.getRestaurantId()))
                .build();

        createOrderItems(orderDto.getOrderItems(), order);

        return orderRepository.save(order);
    }

    private Restaurant findRestaurantById(Long id) {
        return restaurantRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Restaurant", "id", id.toString()));
    }

    public void createOrderItems(List<OrderItemDto> orderItemDtos, Order order) {
        orderItemDtos.forEach(orderItemDto -> {
            var orderItem = OrderItem.builder()
                    .menuItem(findMenuItemById(orderItemDto.getMenuItemId()))
                    .order(order)
                    .quantity(orderItemDto.getQuantity())
                    .additionalInfo(orderItemDto.getAdditionalInfo())
                    .build();

            order.addOrderItem(orderItem);
        });
    }

    private MenuItem findMenuItemById(Long id) {
        return menuItemRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Menu item", "id", id.toString()));
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
                new EntityNotFoundException("Order", "id", id.toString()));
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }


}
