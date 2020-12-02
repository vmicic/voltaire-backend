package com.voltaire.restaurant.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CreateMenuItemRequest {

    private String name;

    private BigDecimal price;

    private String description;

    private String restaurantName;

}
