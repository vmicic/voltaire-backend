package com.voltaire.order.model;

import com.voltaire.restaurant.model.Restaurant;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime orderTime;

    @Column(nullable = false)
    private OrderStatus orderStatus;

    @ManyToOne
    private Restaurant restaurant;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    public void addOrderItem(OrderItem orderItem) {
        if(orderItems == null) {
            orderItems = new ArrayList<>();
        }
        this.orderItems.add(orderItem);
    }

    @Builder
    public Order(LocalDateTime orderTime, OrderStatus orderStatus, Restaurant restaurant, List<OrderItem> orderItems) {
        this.orderTime = orderTime;
        this.orderStatus = orderStatus;
        this.restaurant = restaurant;
        this.orderItems = orderItems;
    }
}
