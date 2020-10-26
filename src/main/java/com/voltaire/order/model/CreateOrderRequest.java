package com.voltaire.order.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CreateOrderRequest {

    private UUID restaurantId;

    List<CreateOrderItemRequest> orderItems;

    private String deliveryAddress;

}
