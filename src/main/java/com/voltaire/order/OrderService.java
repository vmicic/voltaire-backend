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

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final Clock clock;

    public Order createOrder(CreateOrderRequest createOrderRequest) {
        var order = Order.builder()
                .orderTime(LocalDateTime.now(clock))
                .orderStatus(OrderStatus.CREATED)
                .restaurant(findRestaurantById(createOrderRequest.getRestaurantId()))
                .deliveryAddress(createOrderRequest.getDeliveryAddress())
                .build();

        createOrderItems(createOrderRequest.getOrderItems(), order);

        return orderRepository.save(order);
    }

    private Restaurant findRestaurantById(UUID id) {
        return restaurantRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("id", id.toString()));
    }

    private void createOrderItems(List<CreateOrderItemRequest> createOrderItemRequests, Order order) {
        createOrderItemRequests.forEach(createOrderItemRequest -> {
            var orderItem = OrderItem.builder()
                    .menuItem(findMenuItemById(createOrderItemRequest.getMenuItemId()))
                    .order(order)
                    .quantity(createOrderItemRequest.getQuantity())
                    .additionalInfo(createOrderItemRequest.getAdditionalInfo())
                    .build();

            order.addOrderItem(orderItem);
        });
    }

    private MenuItem findMenuItemById(UUID id) {
        return menuItemRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("id", id.toString()));
    }

    public IdResponse confirmOrder(UUID id, Integer minutesForPreparation) {
        Order order = findById(id);
        if (order.notWaitingForResponse()) {
            throw new BadRequestException("Requested order is not waiting response.");
        }

        order.setMinutesForPreparation(minutesForPreparation);
        order.setOrderStatus(OrderStatus.CONFIRMED);
        return new IdResponse(orderRepository.save(order).getId());
    }

    public IdResponse rejectOrder(UUID id) {
        Order order = findById(id);
        if (order.notWaitingForResponse()) {
            throw new BadRequestException("Requested order is not waiting response.");
        }

        order.setOrderStatus(OrderStatus.REJECTED);
        return new IdResponse(orderRepository.save(order).getId());
    }

    public Order findById(UUID id) {
        return orderRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("id", id.toString()));
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }


}
