package com.voltaire.integration;

import com.voltaire.exception.customexceptions.BadRequestException;
import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.order.OrderService;
import com.voltaire.order.model.*;
import com.voltaire.order.repository.OrderRepository;
import com.voltaire.restaurant.model.MenuItem;
import com.voltaire.restaurant.model.Restaurant;
import com.voltaire.restaurant.repository.MenuItemRepository;
import com.voltaire.restaurant.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles(profiles = "inmemorydb-test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderServiceIntegrationInMemoryDbTest {

    private final UUID ID_NOT_EXISTING = UUID.fromString("91326b2b-d6ff-4d7d-abf4-49d1117b395d");

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private Clock clock;

    @Autowired
    private OrderService orderService;


    private Order order;
    private CreateOrderRequest createOrderRequest;
    private UUID orderId;

    @BeforeEach
    public void setUp() {
        var restaurant = Restaurant.builder()
                .name("My restaurant")
                .address("Brace Ribnikar 10")
                .openingTime(LocalTime.of(10, 10))
                .closingTime(LocalTime.of(20, 20))
                .build();

        restaurant = restaurantRepository.save(restaurant);

        var menuItem = MenuItem.builder()
                .name("Burger")
                .price(BigDecimal.valueOf(100.0))
                .description("ketchup, cheese")
                .build();

        menuItem = menuItemRepository.save(menuItem);

        this.order = Order.builder()
                .orderTime(LocalDateTime.now(clock))
                .orderStatus(OrderStatus.CREATED)
                .restaurant(restaurant)
                .build();

        var orderItem = OrderItem.builder()
                .menuItem(menuItem)
                .quantity(2)
                .additionalInfo("no ketchup")
                .order(order)
                .build();

        this.order.addOrderItem(orderItem);

        this.order = orderRepository.save(order);
        this.orderId = this.order.getId();

        var createOrderItemRequest = CreateOrderItemRequest.builder()
                .menuItemId(menuItem.getId())
                .quantity(orderItem.getQuantity())
                .additionalInfo(orderItem.getAdditionalInfo())
                .build();

        var createOrderItemRequests = List.of(createOrderItemRequest);

        this.createOrderRequest = CreateOrderRequest.builder()
                .restaurantId(restaurant.getId())
                .orderItems(createOrderItemRequests)
                .build();

    }

    @Test
    @Transactional
    void createOrderTest() {
        var createdOrder = orderService.createOrder(createOrderRequest);

        order.setId(createdOrder.getId());
        order.getOrderItems().get(0).setId(createdOrder.getOrderItems().get(0).getId());

        createdOrder.setOrderItems(new ArrayList<>(createdOrder.getOrderItems()));
        order.setOrderItems(new ArrayList<>(order.getOrderItems()));

        assertEquals(order, createdOrder);
    }

    @Test
    void createOrderThrowNotFoundExceptionForRestaurant() {
        createOrderRequest.setRestaurantId(ID_NOT_EXISTING);

        assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(createOrderRequest));
    }

    @Test
    void createOrderThrowNotFoundExceptionForMenuItem() {
        createOrderRequest.getOrderItems().get(0).setMenuItemId(ID_NOT_EXISTING);

        assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(createOrderRequest));
    }

    @Test
    void confirmOrderTest() {
        var preparationMinutes = 15;
        var idResponse = orderService.confirmOrder(orderId, preparationMinutes);

        assertEquals(orderId, idResponse.getId());

        var orderDb = orderService.findById(orderId);

        assertEquals(OrderStatus.CONFIRMED, orderDb.getOrderStatus());
        assertEquals(preparationMinutes, orderDb.getMinutesForPreparation());
    }

    @Test
    void confirmOrderNotWaitingConfirmationThrowBadRequestException() {
        order.setOrderStatus(OrderStatus.REJECTED);
        orderRepository.save(order);

        assertThrows(BadRequestException.class, () -> orderService.confirmOrder(orderId, 15));
    }

    @Test
    void confirmOrderThrowNotFoundExceptionForOrder() {
        assertThrows(EntityNotFoundException.class, () -> orderService.confirmOrder(ID_NOT_EXISTING, 15));
    }

    @Test
    @Transactional
    void rejectOrderTest() {
        var idResponse = orderService.rejectOrder(order.getId());

        assertEquals(order.getId(), idResponse.getId());

        var orderDb = orderService.findById(order.getId());

        assertEquals(OrderStatus.REJECTED, orderDb.getOrderStatus());

        order.setOrderStatus(OrderStatus.REJECTED);
        assertEquals(order, orderDb);
    }

    @Test
    void rejectOrderNotWaitingConfirmationThrowBadRequestException() {
        order.setOrderStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);

        assertThrows(BadRequestException.class, () -> orderService.rejectOrder(orderId));
    }

    @Test
    void rejectOrderThrowNotFoundExceptionForOrder() {
        assertThrows(EntityNotFoundException.class, () -> orderService.rejectOrder(ID_NOT_EXISTING));
    }

    @Test
    @Transactional
    void findByIdTest() {
        var orderDb = orderService.findById(order.getId());

        assertEquals(order, orderDb);
    }

    @Test
    @Transactional
    void findByIdThrowNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> orderService.findById(ID_NOT_EXISTING));
    }


}
