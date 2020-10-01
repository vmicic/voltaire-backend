package com.voltaire.order;

import com.voltaire.restaurant.MenuItem;

public class OrderItemBuilder {

    private MenuItem menuItem;
    private Order order;
    private Integer quantity;
    private String additionalInfo;

    public OrderItemBuilder() {
    }

    public OrderItemBuilder setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
        return this;
    }

    public OrderItemBuilder setOrder(Order order) {
        this.order = order;
        return this;
    }

    public OrderItemBuilder setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public OrderItemBuilder setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
        return this;
    }

    public OrderItem build() {
        return new OrderItem(menuItem, order, quantity, additionalInfo);
    }

}
