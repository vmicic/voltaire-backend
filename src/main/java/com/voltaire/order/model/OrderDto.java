package com.voltaire.order.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class OrderDto {

    private Long restaurantId;

    List<OrderItemDto> orderItems;
}
