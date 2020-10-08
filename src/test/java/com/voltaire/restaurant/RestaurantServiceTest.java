package com.voltaire.restaurant;


import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.restaurant.model.Restaurant;
import com.voltaire.restaurant.repository.RestaurantRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    RestaurantRepository restaurantRepository;

    @InjectMocks
    RestaurantService restaurantService;

    private Restaurant restaurant;

    @BeforeEach
    public void setUp() {
         this.restaurant = Restaurant.builder()
                .name("My restaurant")
                .address("Brace Ribnikar 10")
                .openingTime(LocalTime.of(10, 10))
                .closingTime(LocalTime.of(20, 20))
                .build();
         restaurant.setId(1L);
    }

    @Test
    void createRestaurant_returnCreatedRestaurant_returnCreatedRestaurant() {
        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);

        var newRestaurant = restaurantService.createRestaurant(restaurant);

        assertEquals(restaurant, newRestaurant);
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void updateRestaurant_RestaurantDoesntExist_ExceptionThrown() {
        when(restaurantRepository.findById(1L)).thenThrow(new EntityNotFoundException(Restaurant.class, "id",
                Long.valueOf(1).toString()));

        assertThrows(EntityNotFoundException.class, () -> restaurantService.updateRestaurant(1L, restaurant));
        verify(restaurantRepository).findById(1L);
    }

    @Test
    void updateRestaurant_returnValue_returnIdResponse() {
        when(restaurantRepository.findById(1L)).thenReturn(java.util.Optional.of(restaurant));
        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);

        var idResponse = restaurantService.updateRestaurant(1L, restaurant);

        assertEquals(1L, idResponse.getId());
        verify(restaurantRepository).save(restaurant);
        verify(restaurantRepository).findById(1L);
    }

    @Test
    void deleteRestaurant_RestaurantDoesntExist_ExceptionThrown() {
        when(restaurantRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> restaurantService.deleteRestaurant(1L));
    }

    @Test
    void deleteRestaurant_returnValue_returnIdResponse() {
        when(restaurantRepository.existsById(1L)).thenReturn(true);

        var idResponse = restaurantService.deleteRestaurant(1L);

        assertEquals(1L, idResponse.getId());
        verify(restaurantRepository).deleteById(1L);
    }

    @Test
    void notExists_RestaurantDoesntExist_ReturnTrue() {
        when(restaurantRepository.existsById(1L)).thenReturn(false);

        var notExists = restaurantService.notExists(1L);
        assertTrue(notExists);
        verify(restaurantRepository).existsById(1L);
    }

    @Test
    void notExists_RestaurantExists_ReturnFalse() {
        when(restaurantRepository.existsById(1L)).thenReturn(true);

        var notExists = restaurantService.notExists(1L);
        assertFalse(notExists);
        verify(restaurantRepository).existsById(1L);
    }

    @Test
    void findAll_returnAllRestaurants_returnAllRestaurants() {
        var restaurant2 = Restaurant.builder()
                .name("My restaurant")
                .address("Brace Ribnikar 10")
                .openingTime(LocalTime.of(10, 10))
                .closingTime(LocalTime.of(20, 20))
                .build();

        restaurant2.setId(2L);

        when(restaurantRepository.findAll()).thenReturn(Arrays.asList(restaurant, restaurant2));

        var restaurants = restaurantService.findAll();

        assertEquals(2, restaurants.size());
        verify(restaurantRepository).findAll();
    }

}
