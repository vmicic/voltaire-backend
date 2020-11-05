package com.voltaire.delivery.repository;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.voltaire.delivery.model.DeliveryCompany;
import com.voltaire.restaurant.model.Restaurant;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryCompanyRepository {

    private static final String COLLECTION_NAME = "delivery-companies";

    private final Firestore firestore;

    private CollectionReference getDocumentCollection() {
        return firestore.collection(COLLECTION_NAME);
    }
    @SneakyThrows
    public void save(DeliveryCompany deliveryCompany) {
        getDocumentCollection().document(deliveryCompany.getId()).set(deliveryCompany);
    }

}
