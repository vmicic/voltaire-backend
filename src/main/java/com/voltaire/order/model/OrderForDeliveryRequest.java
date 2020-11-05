package com.voltaire.order.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class OrderForDeliveryRequest {

    private String orderId;

    private String restaurantName;

    private String restaurantAddress;

    private String deliveryAddress;

    private Integer minutesForPreparation;

    private Integer restaurantDistanceInMeters;
}
