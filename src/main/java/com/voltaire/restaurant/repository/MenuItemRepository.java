package com.voltaire.restaurant.repository;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.restaurant.model.MenuItem;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuItemRepository {

    private static final String COLLECTION_NAME = "menu-items";

    private final Firestore firestore;

    private CollectionReference getDocumentCollectionWithRestaurant(String restaurantId) {
        return firestore.collection("restaurants").document(restaurantId).collection(COLLECTION_NAME);
    }


    @SneakyThrows
    public void save(MenuItem menuItem) {
        getDocumentCollectionWithRestaurant(menuItem.getRestaurantId()).document(menuItem.getId()).set(menuItem);
    }

    @SneakyThrows
    public MenuItem findById(String id) {
        var documentSnapshots = firestore.collectionGroup(COLLECTION_NAME).whereEqualTo("idField", id).get().get().getDocuments();
        if(documentSnapshots.isEmpty()) {
            throw new EntityNotFoundException("id", id);
        }

        return documentSnapshots.get(0).toObject(MenuItem.class);
    }

    @SneakyThrows
    public boolean notExistsById(String id) {
        var documentSnapshots = firestore.collectionGroup(COLLECTION_NAME).whereEqualTo("idField", id).get().get().getDocuments();
        return documentSnapshots.isEmpty();
    }

    @SneakyThrows
    public void deleteById(String id) {
        firestore.collectionGroup(COLLECTION_NAME).whereEqualTo("id", id).get().get().getDocuments().get(0).getReference().delete();
    }

}
