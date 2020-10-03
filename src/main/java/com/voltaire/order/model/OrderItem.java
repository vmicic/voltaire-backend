package com.voltaire.order.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.voltaire.restaurant.model.MenuItem;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Data
@NoArgsConstructor
@Entity
public class OrderItem {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private MenuItem menuItem;

    @JsonIgnore
    @ManyToOne
    private Order order;

    private Integer quantity;

    private String additionalInfo;

    private OrderItem(OrderItemBuilder orderItemBuilder) {
        this.menuItem = orderItemBuilder.menuItem;
        this.order = orderItemBuilder.order;
        this.quantity = orderItemBuilder.quantity;
        this.additionalInfo = orderItemBuilder.additionalInfo;
    }

    public OrderItem(MenuItem menuItem, Order order, Integer quantity, String additionalInfo) {
        this.menuItem = menuItem;
        this.order = order;
        this.quantity = quantity;
        this.additionalInfo = additionalInfo;
    }

    public static class OrderItemBuilder {

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
            return new OrderItem(this);
        }

    }
}
