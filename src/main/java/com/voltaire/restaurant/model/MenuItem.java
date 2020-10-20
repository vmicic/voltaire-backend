package com.voltaire.restaurant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class MenuItem {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private BigDecimal price;

    private String description;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Restaurant restaurant;

}
