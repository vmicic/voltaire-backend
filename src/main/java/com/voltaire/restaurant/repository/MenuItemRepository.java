package com.voltaire.restaurant.repository;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.restaurant.model.MenuItem;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemRepository {

    private final Firestore firestore;

    private CollectionReference getDocumentCollectionWithRestaurant(String restaurantId) {
        return firestore.collection("restaurants").document(restaurantId).collection("menu-items");
    }

    private CollectionReference getDocumentCollection() {
        return firestore.collection("menu-items");
    }


    @SneakyThrows
    public void save(String restaurantId, MenuItem menuItem) {
        getDocumentCollectionWithRestaurant(restaurantId).document(menuItem.getId()).set(menuItem);
    }

    @SneakyThrows
    public MenuItem findById(String id) {
        var documentSnapshot = getDocumentCollection().document(id).get().get();
        if (!documentSnapshot.exists()) {
            throw new EntityNotFoundException("id", id);
        }

        return documentSnapshot.toObject(MenuItem.class);
    }

    @SneakyThrows
    public List<MenuItem> findAll() {
        var menuItems = new ArrayList<MenuItem>();
        getDocumentCollection().get().get().getDocuments().forEach(queryDocumentSnapshot -> {
            var menuItem= queryDocumentSnapshot.toObject(MenuItem.class);
            menuItems.add(menuItem);
        });
        return menuItems;
    }

    @SneakyThrows
    public boolean existsById(String id) {
        return getDocumentCollection().document(id).get().get().exists();
    }

    public void deleteById(String id) {
        getDocumentCollection().document(id).delete();
    }

}
