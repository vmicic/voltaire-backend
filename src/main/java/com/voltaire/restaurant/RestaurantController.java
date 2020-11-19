package com.voltaire.restaurant;

import com.voltaire.restaurant.model.CreateRestaurantRequest;
import com.voltaire.restaurant.model.ReadRestaurantWithMenuItemsRequest;
import com.voltaire.restaurant.model.Restaurant;
import com.voltaire.shared.IdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createRestaurant(@RequestBody CreateRestaurantRequest createRestaurantRequest) {
        restaurantService.createRestaurant(createRestaurantRequest);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public List<Restaurant> getAllRestaurants() {
        return restaurantService.findAll();
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_RESTAURANT_OWNER')")
    @GetMapping(value = "/{id}")
    public ReadRestaurantWithMenuItemsRequest getRestaurantById(@PathVariable String id) {
        return restaurantService.findById(id);
    }

    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER')")
    @PutMapping(value = "/{id}")
    public IdResponse updateRestaurant(@PathVariable String id, @RequestBody Restaurant restaurant) {
        return restaurantService.updateRestaurant(id, restaurant);
    }

    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER')")
    @DeleteMapping(value = "/{id}")
    public IdResponse deleteRestaurant(@PathVariable String id) {
        return restaurantService.deleteRestaurant(id);
    }

}
