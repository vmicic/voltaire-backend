package com.voltaire.order;

import com.voltaire.exception.customexceptions.BadRequestException;
import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.order.model.*;
import com.voltaire.order.repository.OrderRepository;
import com.voltaire.restaurant.repository.MenuItemRepository;
import com.voltaire.restaurant.repository.RestaurantRepository;
import com.voltaire.shared.IdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OrderService {


    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final Clock clock;

    public void createOrder(CreateOrderRequest createOrderRequest) {
        if (restaurantRepository.notExists(createOrderRequest.getRestaurantId())) {
            throw new EntityNotFoundException("id", createOrderRequest.getRestaurantId());
        }

        var order = Order.builder()
                .id(UUID.randomUUID().toString())
                .orderTime(new Date())
                .orderStatus(OrderStatus.CREATED)
                .restaurantId(createOrderRequest.getRestaurantId())
                .deliveryAddress(createOrderRequest.getDeliveryAddress())
                .build();

        var orderItems = createOrderItems(order.getId(), createOrderRequest.getOrderItems());
        orderRepository.save(order, orderItems);
    }

    private List<OrderItem> createOrderItems(String orderId, List<CreateOrderItemRequest> createOrderItemRequests) {
        var orderItems = new ArrayList<OrderItem>();

        createOrderItemRequests.forEach(createOrderItemRequest -> {
            if (menuItemRepository.notExistsById(createOrderItemRequest.getMenuItemId())) {
                throw new EntityNotFoundException("id", createOrderItemRequest.getMenuItemId());
            }

            var orderItem = OrderItem.builder()
                    .id(UUID.randomUUID().toString())
                    .menuItemId(createOrderItemRequest.getMenuItemId())
                    .quantity(createOrderItemRequest.getQuantity())
                    .additionalInfo(createOrderItemRequest.getAdditionalInfo())
                    .orderId(orderId)
                    .build();

            orderItems.add(orderItem);
        });

        return orderItems;
    }


    public IdResponse confirmOrder(String id, Integer minutesForPreparation) {
        Order order = findById(id);
        if (order.notWaitingForResponse()) {
            throw new BadRequestException("Requested order is not waiting response.");
        }

        order.setMinutesForPreparation(minutesForPreparation);
        order.setOrderStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
        return new IdResponse(id);
    }

    public IdResponse rejectOrder(String id) {
        Order order = findById(id);
        if (order.notWaitingForResponse()) {
            throw new BadRequestException("Requested order is not waiting response.");
        }

        order.setOrderStatus(OrderStatus.REJECTED);
        orderRepository.save(order);
        return new IdResponse(id);
    }

    public Order findById(String id) {
        return orderRepository.findById(id);
    }

}
