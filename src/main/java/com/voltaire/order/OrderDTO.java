package com.voltaire.order;

import java.util.List;

public class OrderDTO {

    private Long restaurantId;

    List<OrderItemDTO> orderItems;

    public OrderDTO() {
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItemDTOs) {
        this.orderItems = orderItemDTOs;
    }
}
