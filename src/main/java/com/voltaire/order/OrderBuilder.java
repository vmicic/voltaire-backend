package com.voltaire.order;

import com.voltaire.restaurant.Restaurant;

import java.time.LocalDateTime;

public class OrderBuilder {

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
        return new Order(orderTime, orderStatus, restaurant);
    }
}
