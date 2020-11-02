package com.voltaire.restaurant;

import com.voltaire.restaurant.model.CreateRestaurantRequest;
import com.voltaire.restaurant.model.Restaurant;
import com.voltaire.shared.IdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Restaurant createRestaurant(@RequestBody CreateRestaurantRequest createRestaurantRequest) {
        return restaurantService.createRestaurant(createRestaurantRequest);
    }

    @GetMapping
    public List<Restaurant> getAllRestaurants() {
        return restaurantService.findAll();
    }

    @GetMapping(value = "/{id}")
    public Restaurant getRestaurantById(@PathVariable UUID id) {
        return restaurantService.findById(id);
    }

    @PutMapping(value = "/{id}")
    public IdResponse updateRestaurant(@PathVariable UUID id, @RequestBody Restaurant restaurant) {
        return restaurantService.updateRestaurant(id, restaurant);
    }

    @DeleteMapping(value = "/{id}")
    public IdResponse deleteRestaurant(@PathVariable UUID id) {
        return restaurantService.deleteRestaurant(id);
    }

}
