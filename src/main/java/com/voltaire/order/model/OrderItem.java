package com.voltaire.order.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.voltaire.restaurant.model.MenuItem;
import lombok.Builder;
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

    @Builder
    public OrderItem(MenuItem menuItem, Order order, Integer quantity, String additionalInfo) {
        this.menuItem = menuItem;
        this.order = order;
        this.quantity = quantity;
        this.additionalInfo = additionalInfo;
    }
}