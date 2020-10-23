package com.voltaire.restaurant.model;

import lombok.Data;

import java.time.LocalTime;

@Data
public class CreateRestaurantRequest {

    private String name;

    private String address;

    private LocalTime openingTime;

    private LocalTime closingTime;
}
