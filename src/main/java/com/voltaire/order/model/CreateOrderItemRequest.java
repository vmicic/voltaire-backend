package com.voltaire.order.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CreateOrderItemRequest {

    private UUID menuItemId;

    private Integer quantity;

    private String additionalInfo;

}
