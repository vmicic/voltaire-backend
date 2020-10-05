package com.voltaire.restaurant;

import com.voltaire.error.customerrors.EntityNotFoundException;
import com.voltaire.restaurant.model.Restaurant;
import com.voltaire.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    public Restaurant findById(Long id) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(id);
        if(restaurant.isEmpty()) {
            throw new EntityNotFoundException(Restaurant.class, "id", id.toString());
        }

        return restaurant.get();
    }

    public boolean notExists(Long id) {
        return !restaurantRepository.existsById(id);
    }

    public void updateRestaurant(Long id, Restaurant restaurant) {
        if(notExists(id)) {
            throw new EntityNotFoundException(Restaurant.class, "id", id.toString());
        }

        restaurant.setId(id);
        restaurantRepository.save(restaurant);
    }

    public void deleteRestaurant(Long id) {
        if(notExists(id)) {
            throw new EntityNotFoundException(Restaurant.class, "id", id.toString());
        }

        restaurantRepository.deleteById(id);
    }

}
