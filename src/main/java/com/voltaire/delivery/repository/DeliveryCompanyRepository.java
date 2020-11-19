package com.voltaire.delivery.repository;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.voltaire.delivery.model.DeliveryCompany;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryCompanyRepository {

    private static final String DELIVERY_COMPANY_COLLECTION_NAME = "delivery-companies";

    private final Firestore firestore;

    private CollectionReference getCollectionReference() {
        return firestore.collection(DELIVERY_COMPANY_COLLECTION_NAME);
    }

    @SneakyThrows
    public void save(DeliveryCompany deliveryCompany) {
        getCollectionReference().document(deliveryCompany.getId()).set(deliveryCompany);
    }

}
