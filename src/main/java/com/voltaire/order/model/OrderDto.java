package com.voltaire.order.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class OrderDto {

    private UUID restaurantId;

    List<OrderItemDto> orderItems;
}
