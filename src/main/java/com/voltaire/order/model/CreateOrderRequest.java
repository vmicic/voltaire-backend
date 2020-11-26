package com.voltaire.order.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CreateOrderRequest {

    private String restaurantId;

    List<CreateOrderItemRequest> orderItems;

}
