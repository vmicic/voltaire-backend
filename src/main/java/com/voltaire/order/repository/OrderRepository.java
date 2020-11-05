package com.voltaire.order.repository;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.order.model.Order;
import com.voltaire.order.model.OrderItem;
import com.voltaire.order.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log
@Service
@RequiredArgsConstructor
public class OrderRepository {

    private final Firestore firestore;

    private static final String ORDER_COLLECTION_NAME = "orders";
    private static final String ORDER_ITEMS_SUB_COLLECTION_NAME = "order-items";

    private CollectionReference getDocumentCollection() {
        return firestore.collection(ORDER_COLLECTION_NAME);
    }

    @SneakyThrows
    public void save(Order order) {
        getDocumentCollection().document(order.getId()).set(order);
    }

    @SneakyThrows
    public void save(Order order, List<OrderItem> orderItems) {
        var batch = firestore.batch();

        var orderRef = getDocumentCollection().document(order.getId());
        batch.set(orderRef, order);

        orderItems.forEach(orderItem -> {
            var orderItemRef = getDocumentCollection().document(order.getId()).collection(ORDER_ITEMS_SUB_COLLECTION_NAME).document(orderItem.getId());
            batch.set(orderItemRef, orderItem);
        });

        batch.commit();
    }

    @SneakyThrows
    public Order findById(String id) {
        var documentSnapshot = getDocumentCollection().document(id).get().get();
        if (!documentSnapshot.exists()) {
            throw new EntityNotFoundException("id", id);
        }

        return documentSnapshot.toObject(Order.class);
    }

    @SneakyThrows
    public List<Order> findAllOrdersForDelivery(Date timeCutoff) {
        var queryDocumentSnapshots = getDocumentCollection()
                .whereGreaterThan("orderTime", timeCutoff)
                .whereEqualTo("orderStatus", OrderStatus.CONFIRMED)
                .get().get().getDocuments();

        var orders = new ArrayList<Order>();
        queryDocumentSnapshots.forEach(queryDocumentSnapshot -> {
            var order = queryDocumentSnapshot.toObject(Order.class);
            orders.add(order);
        });

        return orders;
    }

}
