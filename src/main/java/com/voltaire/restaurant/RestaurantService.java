package com.voltaire.restaurant;

import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.restaurant.model.Restaurant;
import com.voltaire.restaurant.repository.RestaurantRepository;
import com.voltaire.shared.IdResponse;
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

    public boolean notExists(Long id) {
        return !restaurantRepository.existsById(id);
    }

    public IdResponse updateRestaurant(Long id, Restaurant restaurant) {

        Restaurant oldRestaurant = findById(id);

        oldRestaurant.setName(restaurant.getName());
        oldRestaurant.setAddress(restaurant.getAddress());
        oldRestaurant.setClosingTime(restaurant.getClosingTime());
        oldRestaurant.setOpeningTime(restaurant.getOpeningTime());

        return new IdResponse(restaurantRepository.save(oldRestaurant).getId());
    }

    public IdResponse deleteRestaurant(Long id) {
        if (notExists(id)) {
            throw new EntityNotFoundException("id", id.toString());
        }
        restaurantRepository.deleteById(id);
        return new IdResponse(id);
    }

    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    public Restaurant findById(Long id) {
        return restaurantRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("id", id.toString()));
    }

}
