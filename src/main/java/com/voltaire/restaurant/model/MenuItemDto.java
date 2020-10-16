package com.voltaire.restaurant.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
public class MenuItemDto {

    private String name;

    private BigDecimal price;

    private String description;

    private UUID restaurantId;

}
