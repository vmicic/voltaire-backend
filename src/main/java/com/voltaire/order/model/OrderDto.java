package com.voltaire.order.model;

import java.util.List;

public class OrderDto {

    private Long restaurantId;

    List<OrderItemDto> orderItems;

    public OrderDto() {
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public List<OrderItemDto> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDto> orderItemDtos) {
        this.orderItems = orderItemDtos;
    }
}
