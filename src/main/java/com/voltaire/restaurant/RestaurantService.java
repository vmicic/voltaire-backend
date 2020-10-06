package com.voltaire.restaurant;

import com.voltaire.exceptions.customexceptions.EntityNotFoundException;
import com.voltaire.restaurant.model.Restaurant;
import com.voltaire.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return restaurantRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(Restaurant.class, "id", id.toString()));
    }

    public boolean notExists(Long id) {
        return !restaurantRepository.existsById(id);
    }

    public IdResponse updateRestaurant(Long id, Restaurant restaurant) {
        if (notExists(id)) {
            throw new EntityNotFoundException(Restaurant.class, "id", id.toString());
        }

        restaurant.setId(id);
        return new IdResponse(restaurantRepository.save(restaurant).getId());
    }

    public IdResponse deleteRestaurant(Long id) {
        if (notExists(id)) {
            throw new EntityNotFoundException(Restaurant.class, "id", id.toString());
        }
        restaurantRepository.deleteById(id);
        return new IdResponse(id);
    }

}
