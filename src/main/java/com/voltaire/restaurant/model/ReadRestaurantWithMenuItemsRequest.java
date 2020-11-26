package com.voltaire.restaurant.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ReadRestaurantWithMenuItemsRequest {

    private String restaurantId;

    private String name;

    private String address;

    private String openingTime;

    private String closingTime;

    private List<ReadMenuItemRequest> menuItems;
}
