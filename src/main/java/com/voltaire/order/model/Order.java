package com.voltaire.order.model;

import com.voltaire.restaurant.model.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Order {

    private UUID id;

    private LocalDateTime orderTime;

    private OrderStatus orderStatus;

    private Restaurant restaurant;

    private List<OrderItem> orderItems;

    private Integer minutesForPreparation;

    private String deliveryAddress;

    public void addOrderItem(OrderItem orderItem) {
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }
        this.orderItems.add(orderItem);
    }

    public boolean notWaitingForResponse() {
        return !this.orderStatus.equals(OrderStatus.CREATED);
    }

    public boolean notWaitingDeliveryService() {
        return !this.orderStatus.equals(OrderStatus.CONFIRMED);
    }

    public boolean notWaitingDeliveryConfirmation() {
        return !this.orderStatus.equals(OrderStatus.DELIVERING);
    }

    public boolean notWaitingDeliveryStart() {
        return !this.orderStatus.equals(OrderStatus.PREPARING);
    }
}
