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
public class CreateOrderItemRequest {

    private UUID menuItemId;

    private Integer quantity;

    private String additionalInfo;

}
