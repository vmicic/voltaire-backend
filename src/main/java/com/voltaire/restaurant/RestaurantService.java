package com.voltaire.restaurant;

import java.util.List;

public interface RestaurantService {

    Restaurant createRestaurant(Restaurant restaurant);

    List<Restaurant> findAll();

    Restaurant findById(Long id);

    boolean notExists(Long id);

    void updateRestaurant(Long id, Restaurant restaurant);

    void deleteRestaurant(Long id);
}
