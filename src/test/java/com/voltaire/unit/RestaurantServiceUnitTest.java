package com.voltaire.unit;


import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.restaurant.RestaurantService;
import com.voltaire.restaurant.model.CreateRestaurantRequest;
import com.voltaire.restaurant.model.Restaurant;
import com.voltaire.restaurant.repository.RestaurantRepository;
import com.voltaire.shared.GeocodeService;
import com.voltaire.shared.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceUnitTest {

    private final UUID ID_NOT_EXISTING = UUID.fromString("0fc3d32a-38c0-457c-b34f-8478fb92f3f4");

    @Mock
    RestaurantRepository restaurantRepository;

    @Mock
    GeocodeService geocodeService;

    @InjectMocks
    RestaurantService restaurantService;

    private Restaurant restaurant;

    @BeforeEach
    public void setUp() {
        var point = new Point(19.8371365, 45.2479144);

        this.restaurant = Restaurant.builder()
                .id(UUID.fromString("f9822d37-7357-4bd7-9ad7-e16b68da2e7c"))
                .name("My restaurant")
                .address("Brace Ribnikar 10, Novi Sad")
                .openingTime(LocalTime.of(10, 10))
                .closingTime(LocalTime.of(20, 20))
                .point(point)
                .build();
    }

    @Test
    void createRestaurantTest() {
        var point = new Point(19.8371365, 45.2479144);

        var expectedRestaurantToSave = Restaurant.builder()
                .name("My restaurant")
                .address("Brace Ribnikar 10, Novi Sad")
                .openingTime(LocalTime.of(10, 10))
                .closingTime(LocalTime.of(20, 20))
                .point(point)
                .build();

        var createRestaurantRequest = CreateRestaurantRequest.builder()
                .address("Brace Ribnikar 10, Novi Sad")
                .name("My restaurant")
                .openingTime(LocalTime.of(10, 10))
                .closingTime(LocalTime.of(20, 20))
                .build();

        doReturn(restaurant).when(restaurantRepository).save(expectedRestaurantToSave);
        doReturn(point).when(geocodeService).getPointForAddressString(createRestaurantRequest.getAddress());

        var newRestaurant = restaurantService.createRestaurant(createRestaurantRequest);

        assertEquals(restaurant, newRestaurant);
        verify(restaurantRepository).save(expectedRestaurantToSave);
    }

    @Test
    void updateRestaurantThrowNotFoundException() {
        when(restaurantRepository.findById(ID_NOT_EXISTING)).thenThrow(new EntityNotFoundException("id",
                ID_NOT_EXISTING.toString()));

        assertThrows(EntityNotFoundException.class, () -> restaurantService.updateRestaurant(ID_NOT_EXISTING, restaurant));
        verify(restaurantRepository).findById(ID_NOT_EXISTING);
    }

    @Test
    void updateRestaurantTest() {
        var updateId = restaurant.getId();

        when(restaurantRepository.findById(updateId)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);

        var idResponse = restaurantService.updateRestaurant(updateId, restaurant);

        assertEquals(updateId, idResponse.getId());
        verify(restaurantRepository).save(restaurant);
        verify(restaurantRepository).findById(updateId);
    }

    @Test
    void deleteRestaurantThrowNotFoundException() {
        when(restaurantRepository.existsById(ID_NOT_EXISTING)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> restaurantService.deleteRestaurant(ID_NOT_EXISTING));
    }

    @Test
    void deleteRestaurantTest() {
        var deleteId = restaurant.getId();

        when(restaurantRepository.existsById(deleteId)).thenReturn(true);

        var idResponse = restaurantService.deleteRestaurant(deleteId);

        assertEquals(deleteId, idResponse.getId());
        verify(restaurantRepository).deleteById(deleteId);
    }

    @Test
    void notExistsTrueTest() {
        when(restaurantRepository.existsById(ID_NOT_EXISTING)).thenReturn(false);

        var notExists = restaurantService.notExists(ID_NOT_EXISTING);
        assertTrue(notExists);
        verify(restaurantRepository).existsById(ID_NOT_EXISTING);
    }

    @Test
    void notExistsFalseTest() {
        var existId = restaurant.getId();

        when(restaurantRepository.existsById(existId)).thenReturn(true);

        var notExists = restaurantService.notExists(existId);
        assertFalse(notExists);
        verify(restaurantRepository).existsById(existId);
    }

    @Test
    void findAllReturnAllRestaurants() {
        var restaurant2 = Restaurant.builder()
                .name("My restaurant")
                .address("Brace Ribnikar 10")
                .openingTime(LocalTime.of(10, 10))
                .closingTime(LocalTime.of(20, 20))
                .build();

        when(restaurantRepository.findAll()).thenReturn(Arrays.asList(restaurant, restaurant2));

        var restaurants = restaurantService.findAll();

        assertEquals(2, restaurants.size());
        verify(restaurantRepository).findAll();
    }

}
