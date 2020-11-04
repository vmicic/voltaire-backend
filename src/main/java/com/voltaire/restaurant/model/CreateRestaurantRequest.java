package com.voltaire.restaurant.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRestaurantRequest {

    private String name;

    private String address;

    private LocalTime openingTime;

    private LocalTime closingTime;
}
