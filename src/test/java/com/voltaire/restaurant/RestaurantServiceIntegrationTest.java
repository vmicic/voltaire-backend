package com.voltaire.restaurant;

import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.restaurant.model.Restaurant;
import com.voltaire.restaurant.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(profiles = "test")
class RestaurantServiceIntegrationTest {

    private final Long ID_NOT_EXIST = 500L;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private Restaurant restaurant1;

    private Restaurant restaurant2;

    @BeforeEach
    public void setUp() {
        var restaurant = Restaurant.builder()
                .name("My restaurant")
                .address("Brace Ribnikar 10")
                .openingTime(LocalTime.of(10, 10))
                .closingTime(LocalTime.of(20, 20))
                .build();

        this.restaurant1 = restaurantRepository.save(restaurant);

        this.restaurant2 = Restaurant.builder()
                .name("new restaurant")
                .address("Brace Ribnikar 70")
                .openingTime(LocalTime.of(8, 10))
                .closingTime(LocalTime.of(20, 20))
                .build();
    }

    @Test
    @Transactional
    void findByIdTest() {
        var foundRestaurant = restaurantService.findById(restaurant1.getId());

        assertEquals(restaurant1, foundRestaurant);
    }

    @Test
    void findByIdThrowNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> restaurantService.findById(ID_NOT_EXIST));
    }

    @Test
    void createRestaurantReturnCreatedRestaurant() {
        var createdRestaurant = restaurantService.createRestaurant(restaurant2);

        assertEquals(restaurant2, createdRestaurant);
    }

    @Test
    @Transactional
    void updateRestaurantTest() {
        restaurant2.setId(restaurant1.getId());

        var idResponse = restaurantService.updateRestaurant(restaurant1.getId(), restaurant2);

        var updatedRestaurant = restaurantService.findById(restaurant1.getId());

        assertEquals(restaurant1.getId(), idResponse.getId());
        assertEquals(restaurant2, updatedRestaurant);
    }

    @Test
    void updateRestaurantThrowNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> restaurantService.updateRestaurant(ID_NOT_EXIST, restaurant2));
    }

    @Test
    void deleteRestaurantTest() {
        var idResponse = restaurantService.deleteRestaurant(restaurant1.getId());
        var id = restaurant1.getId();

        assertEquals(id, idResponse.getId());
        assertThrows(EntityNotFoundException.class, () -> restaurantService.findById(id));
    }

    @Test
    void deleteRestaurantThrowNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> restaurantService.deleteRestaurant(ID_NOT_EXIST));
    }

    @Test
    void notExistsTrueTest() {
        var notExists = restaurantService.notExists(ID_NOT_EXIST);

        assertTrue(notExists);
    }

    @Test
    void notExistsFalseTest() {
        var notExists = restaurantService.notExists(restaurant1.getId());

        assertFalse(notExists);
    }

}
