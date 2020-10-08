package com.voltaire.restaurant;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RestaurantControllerTest {

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private RestaurantController restaurantController;
}
