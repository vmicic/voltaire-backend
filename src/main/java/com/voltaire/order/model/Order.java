package com.voltaire.order.model;

import com.voltaire.restaurant.model.Restaurant;
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
    private List<OrderItem> orderItems = new ArrayList<>();

    private Order(OrderBuilder orderBuilder) {
        this.orderTime = orderBuilder.orderTime;
        this.orderStatus = orderBuilder.orderStatus;
        this.restaurant = orderBuilder.restaurant;
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
    }

    public static class OrderBuilder {
        private LocalDateTime orderTime;
        private OrderStatus orderStatus;
        private Restaurant restaurant;

        public OrderBuilder() {
        }

        public OrderBuilder setOrderTime(LocalDateTime orderTime) {
            this.orderTime = orderTime;
            return this;
        }

        public OrderBuilder setOrderStatus(OrderStatus orderStatus) {
            this.orderStatus = orderStatus;
            return this;
        }

        public OrderBuilder setRestaurant(Restaurant restaurant) {
            this.restaurant = restaurant;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
