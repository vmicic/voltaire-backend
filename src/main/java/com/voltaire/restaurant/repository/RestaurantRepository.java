package com.voltaire.restaurant.repository;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.voltaire.exception.customexceptions.BadRequestException;
import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.restaurant.model.MenuItem;
import com.voltaire.restaurant.model.ReadMenuItemRequest;
import com.voltaire.restaurant.model.ReadRestaurantWithMenuItemsRequest;
import com.voltaire.restaurant.model.Restaurant;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RestaurantRepository {

    public static final String RESTAURANT_COLLECTION_NAME = "restaurants";

    private final Firestore firestore;

    private CollectionReference getCollectionReference() {
        return firestore.collection(RESTAURANT_COLLECTION_NAME);
    }

    private DocumentReference getRestaurantDocumentReference(String restaurantId) {
        return getCollectionReference().document(restaurantId);
    }

    @SneakyThrows
    public void save(Restaurant restaurant) {
        getCollectionReference().document(restaurant.getId()).set(restaurant);
    }

    @SneakyThrows
    public boolean notExists(String id) {
        return !getRestaurantDocumentReference(id).get().get().exists();
    }

    public void deleteById(String id) {
        getRestaurantDocumentReference(id).delete();
    }


    @SneakyThrows
    public Restaurant findById(String id) {
        var documentSnapshot = getRestaurantDocumentReference(id).get().get();
        if (!documentSnapshot.exists()) {
            throw new EntityNotFoundException("id", id);
        }

        return documentSnapshot.toObject(Restaurant.class);
    }

    @SneakyThrows
    public ReadRestaurantWithMenuItemsRequest findByIdWithMenuItems(String id) {
        var documentSnapshot = getRestaurantDocumentReference(id).get().get();
        if (!documentSnapshot.exists()) {
            throw new EntityNotFoundException("id", id);
        }

        var restaurant = documentSnapshot.toObject(Restaurant.class);

        if (restaurant == null) {
            throw new BadRequestException("Request could not be executed");
        }

        var documents = getRestaurantDocumentReference(id).collection(MenuItemRepository.MENU_ITEM_COLLECTION_NAME).get().get().getDocuments();
        var menuItemsDto = new ArrayList<ReadMenuItemRequest>();
        documents.forEach(document -> {
            var menuItem = document.toObject(MenuItem.class);
            var menuItemDto = new ReadMenuItemRequest();
            menuItemDto.setName(menuItem.getName());
            menuItemDto.setDescription(menuItem.getDescription());
            menuItemDto.setPrice(menuItem.getPrice());
            menuItemsDto.add(menuItemDto);
        });

        var restaurantDto = new ReadRestaurantWithMenuItemsRequest();
        restaurantDto.setName(restaurant.getName());
        restaurantDto.setAddress(restaurant.getAddress());
        restaurantDto.setOpeningTime(restaurant.getOpeningTime());
        restaurantDto.setClosingTime(restaurant.getClosingTime());
        restaurantDto.setMenuItems(menuItemsDto);

        return restaurantDto;
    }

    @SneakyThrows
    public List<Restaurant> findAll() {
        var restaurants = new ArrayList<Restaurant>();
        getCollectionReference().get().get().getDocuments().forEach(queryDocumentSnapshot -> {
            var restaurant = queryDocumentSnapshot.toObject(Restaurant.class);
            restaurants.add(restaurant);
        });
        return restaurants;
    }

    public void updateRestaurant(String id, Restaurant restaurant) {
        Map<String, Object> update = new HashMap<>();
        update.put("name", restaurant.getName());
        update.put("address", restaurant.getAddress());
        update.put("closingTime", restaurant.getClosingTime());
        update.put("openingTime", restaurant.getOpeningTime());

        getRestaurantDocumentReference(id)
                .set(update, SetOptions.merge());
    }
}
