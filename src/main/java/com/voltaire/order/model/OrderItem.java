package com.voltaire.order.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.voltaire.restaurant.model.MenuItem;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    private UUID id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MenuItem menuItem;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Order order;

    private Integer quantity;

    private String additionalInfo;

}
