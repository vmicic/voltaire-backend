package com.voltaire.order.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.voltaire.restaurant.model.MenuItem;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class OrderItem {

    @Id
    @GeneratedValue
    private UUID id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    private MenuItem menuItem;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @ManyToOne
    private Order order;

    private Integer quantity;

    private String additionalInfo;

}
