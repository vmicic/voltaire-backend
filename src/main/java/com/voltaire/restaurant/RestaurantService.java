package com.voltaire.restaurant;

import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.restaurant.model.CreateRestaurantRequest;
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

    public Restaurant createRestaurant(CreateRestaurantRequest createRestaurantRequest) {
        var restaurant = Restaurant.builder()
                .name(createRestaurantRequest.getName())
                .address(createRestaurantRequest.getAddress())
                .openingTime(createRestaurantRequest.getOpeningTime())
                .closingTime(createRestaurantRequest.getClosingTime())
                .geolocation(geocodeService.getGeolocationForAddressString(createRestaurantRequest.getAddress()))
                .build();

        return restaurantRepository.save(restaurant);
    }

    public boolean notExists(UUID id) {
        return !restaurantRepository.existsById(id);
    }

    public IdResponse updateRestaurant(UUID id, Restaurant restaurant) {

        Restaurant oldRestaurant = findById(id);

        oldRestaurant.setName(restaurant.getName());
        oldRestaurant.setAddress(restaurant.getAddress());
        oldRestaurant.setClosingTime(restaurant.getClosingTime());
        oldRestaurant.setOpeningTime(restaurant.getOpeningTime());

        return new IdResponse(restaurantRepository.save(oldRestaurant).getId());
    }

    public IdResponse deleteRestaurant(UUID id) {
        if (notExists(id)) {
            throw new EntityNotFoundException("id", id.toString());
        }
        restaurantRepository.deleteById(id);
        return new IdResponse(id);
    }

    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    public Restaurant findById(UUID id) {
        return restaurantRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("id", id.toString()));
    }

}
