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
public class OrderForDelivery {

    private UUID orderId;

    private String restaurantName;

    private String address;

    private Integer minutes;
}
