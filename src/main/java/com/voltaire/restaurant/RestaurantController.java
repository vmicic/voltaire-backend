package com.voltaire.restaurant;

import com.voltaire.restaurant.model.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant) {
        Restaurant newRestaurant = restaurantService.createRestaurant(restaurant);

        return new ResponseEntity<>(newRestaurant, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantService.findAll();

        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long id) {
        Restaurant restaurant = restaurantService.findById(id);

        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<IdResponse> updateRestaurant(@PathVariable Long id, @RequestBody Restaurant restaurant) {
        var restaurantId = restaurantService.updateRestaurant(id, restaurant);

        var idResponse = new IdResponse(restaurantId);

        return new ResponseEntity<>(idResponse, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<IdResponse> deleteRestaurant(@PathVariable Long id) {
        var restaurantId = restaurantService.deleteRestaurant(id);

        var idResponse = new IdResponse(restaurantId);

        return new ResponseEntity<>(idResponse, HttpStatus.OK);
    }

}
