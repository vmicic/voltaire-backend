package com.voltaire.restaurant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final MenuItemService menuItemService;

    public RestaurantController(RestaurantService restaurantService, MenuItemService menuItemService) {
        this.restaurantService = restaurantService;
        this.menuItemService = menuItemService;
    }

    @PostMapping
    public ResponseEntity<?> createRestaurant(@RequestBody Restaurant restaurant) {
        Restaurant newRestaurant = restaurantService.createRestaurant(restaurant);

        return new ResponseEntity<>(newRestaurant, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantService.findAll();

        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRestaurantById(@PathVariable Long id) {
        if(restaurantService.notExists(id)) {
            return new ResponseEntity<>("Requested restaurant not found", HttpStatus.NOT_FOUND);
        }

        Restaurant restaurant = restaurantService.findById(id);

        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRestaurant(@PathVariable Long id, @RequestBody Restaurant restaurant) {
        if(restaurantService.notExists(id)) {
            return new ResponseEntity<>("Requested restaurant not found", HttpStatus.NOT_FOUND);
        }

        restaurantService.updateRestaurant(id, restaurant);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRestaurant(@PathVariable Long id) {
        if(restaurantService.notExists(id)) {
            return new ResponseEntity<>("Requested restaurant not found", HttpStatus.NOT_FOUND);
        }
        


        restaurantService.deleteRestaurant(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
