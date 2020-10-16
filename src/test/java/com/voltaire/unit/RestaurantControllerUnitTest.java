package com.voltaire.unit;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.voltaire.restaurant.RestaurantController;
import com.voltaire.restaurant.RestaurantService;
import com.voltaire.restaurant.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = RestaurantController.class)
class RestaurantControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;

    @Autowired
    private ObjectMapper objectMapper;

    private Restaurant restaurant;
    private Restaurant restaurant2;

    @BeforeEach
    public void setUp() {
        this.restaurant = Restaurant.builder()
                .name("My restaurant")
                .address("Brace Ribnikar 10")
                .openingTime(LocalTime.of(10, 10))
                .closingTime(LocalTime.of(20, 20))
                .build();
        restaurant.setId(UUID.fromString("f9822d37-7357-4bd7-9ad7-e16b68da2e7c"));
    }

    @Test
    void createRestaurantReturnCreatedRestaurant() throws Exception {
        when(restaurantService.createRestaurant(restaurant)).thenReturn(restaurant);

        var result = mockMvc.perform(post("/v1/restaurants")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(restaurant)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var response = objectMapper.readValue(result, Restaurant.class);
        assertEquals(restaurant, response);
    }


}
