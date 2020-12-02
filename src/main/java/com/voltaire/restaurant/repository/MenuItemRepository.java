package com.voltaire.restaurant.repository;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.restaurant.model.MenuItem;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuItemRepository {

    public static final String MENU_ITEM_COLLECTION_NAME = "menu-items";
    private static final String ID_FIELD_NAME = "idField";

    private final Firestore firestore;

    private CollectionReference getMenuItemsCollectionReference(String restaurantId) {
        return firestore.collection(RestaurantRepository.RESTAURANT_COLLECTION_NAME).document(restaurantId).collection(MENU_ITEM_COLLECTION_NAME);
    }

    private DocumentReference getMenuItemDocumentReference(String restaurantId, String menuItemId) {
        return getMenuItemsCollectionReference(restaurantId).document(menuItemId);
    }

    @SneakyThrows
    public void save(String restaurantId, MenuItem menuItem) {
        getMenuItemDocumentReference(restaurantId, menuItem.getId()).set(menuItem);
    }

    @SneakyThrows
    public MenuItem findById(String id) {
        var queryDocumentSnapshots = firestore.collectionGroup(MENU_ITEM_COLLECTION_NAME).whereEqualTo(ID_FIELD_NAME, id).get().get().getDocuments();
        if (queryDocumentSnapshots.isEmpty()) {
            throw new EntityNotFoundException("id", id);
        }

        return queryDocumentSnapshots.get(0).toObject(MenuItem.class);
    }

    @SneakyThrows
    public boolean notExistsById(String id) {
        var queryDocumentSnapshots = firestore.collectionGroup(MENU_ITEM_COLLECTION_NAME).whereEqualTo(ID_FIELD_NAME, id).get().get().getDocuments();
        return queryDocumentSnapshots.isEmpty();
    }

    @SneakyThrows
    public void deleteById(String id) {
        firestore.collectionGroup(MENU_ITEM_COLLECTION_NAME).whereEqualTo(ID_FIELD_NAME, id).get().get().getDocuments().get(0).getReference().delete();
    }

}
