package com.voltaire.order.model;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @DocumentId
    private String id;

    private String menuItemId;

    private Integer quantity;

    private String additionalInfo;

    private String orderId;

}
