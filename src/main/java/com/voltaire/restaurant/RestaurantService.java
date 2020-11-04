package com.voltaire.restaurant;

import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.restaurant.model.CreateRestaurantRequest;
import com.voltaire.restaurant.model.ReadRestaurantWithMenuItemsRequest;
import com.voltaire.restaurant.model.Restaurant;
import com.voltaire.restaurant.repository.RestaurantRepository;
import com.voltaire.shared.GeocodeService;
import com.voltaire.shared.IdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RestaurantService {

    private final GeocodeService geocodeService;

    private final RestaurantRepository restaurantRepository;

    public void createRestaurant(CreateRestaurantRequest createRestaurantRequest) {
        var restaurant = Restaurant.builder()
                .id(UUID.randomUUID().toString())
                .name(createRestaurantRequest.getName())
                .address(createRestaurantRequest.getAddress())
                .openingTime(createRestaurantRequest.getOpeningTime().toString())
                .closingTime(createRestaurantRequest.getClosingTime().toString())
                .geolocation(geocodeService.getGeolocationForAddressString(createRestaurantRequest.getAddress()))
                .build();

        restaurantRepository.save(restaurant);
    }

    public boolean notExists(String id) {
        return !restaurantRepository.existsById(id);
    }

    public IdResponse updateRestaurant(String id, Restaurant restaurant) {
        restaurantRepository.updateRestaurant(id, restaurant);

        return new IdResponse(id);
    }

    public IdResponse deleteRestaurant(String id) {
        if (notExists(id)) {
            throw new EntityNotFoundException("id", id);
        }
        restaurantRepository.deleteById(id);
        return new IdResponse(id);
    }

    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    public ReadRestaurantWithMenuItemsRequest findById(String id) {
        return restaurantRepository.findById(id);
    }


}
