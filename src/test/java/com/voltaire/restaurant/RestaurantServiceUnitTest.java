package com.voltaire.restaurant;


import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.restaurant.model.Restaurant;
import com.voltaire.restaurant.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceUnitTest {

    private static final Long ID_NOT_EXIST = 500L;

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
    void createRestaurantReturnCreatedRestaurant() {
        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);

        var newRestaurant = restaurantService.createRestaurant(restaurant);

        assertEquals(restaurant, newRestaurant);
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void updateRestaurantThrowNotFoundException() {
        when(restaurantRepository.findById(ID_NOT_EXIST)).thenThrow(new EntityNotFoundException(Restaurant.class, "id",
                ID_NOT_EXIST.toString()));

        assertThrows(EntityNotFoundException.class, () -> restaurantService.updateRestaurant(ID_NOT_EXIST, restaurant));
        verify(restaurantRepository).findById(ID_NOT_EXIST);
    }

    @Test
    void updateRestaurantTest() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);

        var idResponse = restaurantService.updateRestaurant(1L, restaurant);

        assertEquals(1L, idResponse.getId());
        verify(restaurantRepository).save(restaurant);
        verify(restaurantRepository).findById(1L);
    }

    @Test
    void deleteRestaurantThrowNotFoundException() {
        when(restaurantRepository.existsById(ID_NOT_EXIST)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> restaurantService.deleteRestaurant(ID_NOT_EXIST));
    }

    @Test
    void deleteRestaurantTest() {
        when(restaurantRepository.existsById(1L)).thenReturn(true);

        var idResponse = restaurantService.deleteRestaurant(1L);

        assertEquals(1L, idResponse.getId());
        verify(restaurantRepository).deleteById(1L);
    }

    @Test
    void notExistsTrueTest() {
        when(restaurantRepository.existsById(1L)).thenReturn(false);

        var notExists = restaurantService.notExists(1L);
        assertTrue(notExists);
        verify(restaurantRepository).existsById(1L);
    }

    @Test
    void notExistsFalseTest() {
        when(restaurantRepository.existsById(1L)).thenReturn(true);

        var notExists = restaurantService.notExists(1L);
        assertFalse(notExists);
        verify(restaurantRepository).existsById(1L);
    }

    @Test
    void findAllReturnAllRestaurants() {
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
