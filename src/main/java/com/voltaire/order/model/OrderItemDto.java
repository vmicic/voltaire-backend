package com.voltaire.order.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderItemDto {

    private Long menuItemId;

    private Integer quantity;

    private String additionalInfo;

}
