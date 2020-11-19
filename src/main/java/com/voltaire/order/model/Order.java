package com.voltaire.order.model;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Order {

    @DocumentId
    private String id;

    private Date orderTime;

    private OrderStatus orderStatus;

    private String restaurantId;

    private Integer minutesForPreparation;

    private String deliveryAddress;

    public boolean notWaitingForResponse() {
        return !this.orderStatus.equals(OrderStatus.CREATED);
    }

    public boolean notWaitingDeliveryService() {
        return !this.orderStatus.equals(OrderStatus.CONFIRMED);
    }

    public boolean notWaitingDeliveryConfirmation() {
        return !this.orderStatus.equals(OrderStatus.DELIVERING);
    }

    public boolean notWaitingDeliveryStart() {
        return !this.orderStatus.equals(OrderStatus.PREPARING);
    }
}
