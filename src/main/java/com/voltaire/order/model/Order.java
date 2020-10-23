package com.voltaire.order.model;

import com.voltaire.restaurant.model.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    private UUID id;

    private LocalDateTime orderTime;

    @Column(nullable = false)
    private OrderStatus orderStatus;

    @ManyToOne
    private Restaurant restaurant;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    private Integer minutesForPreparation;

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
