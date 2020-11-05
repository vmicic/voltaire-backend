package com.voltaire.order.repository;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.voltaire.order.model.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemRepository {

    private final Firestore firestore;

    private CollectionReference getDocumentCollectionWithRestaurant(String orderId) {
        return firestore.collection("orders").document(orderId).collection("order-items");
    }

    public void saveAll(List<OrderItem> orderItems) {
        orderItems.forEach(orderItem -> {
            getDocumentCollectionWithRestaurant(orderItem.getOrderId()).document(orderItem.getId()).set(orderItem);
        });
    }
}
