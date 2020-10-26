package com.voltaire.integration;

import com.voltaire.delivery.DeliveryService;
import com.voltaire.exception.customexceptions.BadRequestException;
import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.order.model.Order;
import com.voltaire.order.model.OrderItem;
import com.voltaire.order.model.OrderStatus;
import com.voltaire.order.repository.OrderRepository;
import com.voltaire.restaurant.model.MenuItem;
import com.voltaire.restaurant.model.Restaurant;
import com.voltaire.restaurant.repository.MenuItemRepository;
import com.voltaire.restaurant.repository.RestaurantRepository;
import com.voltaire.shared.Geolocation;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(profiles = "inmemorydb-test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DeliveryServiceIntegrationInMemoryDbTest {

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
    private DeliveryService deliveryService;

    private Order order;
    private UUID orderId;

    @BeforeEach
    public void setUp() {
        var restaurant = Restaurant.builder()
                .name("My restaurant")
                .address("Brace Ribnikar 10")
                .openingTime(LocalTime.of(10, 10))
                .closingTime(LocalTime.of(20, 20))
                .geolocation(new Geolocation(19.8371365, 45.2479144))
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
    }

    @Test
    void getOrdersForDeliveryTest() {
        order.setOrderStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        var ordersForDelivery = deliveryService.getOrdersForDelivery();

        assertEquals(1, ordersForDelivery.size());
    }

    @Test
    void getOrdersForDeliveryDontTakeOrdersWithWrongStatusTest() {
        order.setOrderStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);

        var ordersForDelivery = deliveryService.getOrdersForDelivery();

        assertEquals(0, ordersForDelivery.size());
    }

    @Test
    @Transactional
    void takeOrderToDeliverTest() {
        order.setOrderStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        deliveryService.takeOrderToDeliver(order.getId());

        var orderDb = deliveryService.findById(order.getId());

        assertEquals(OrderStatus.PREPARING, orderDb.getOrderStatus());

        order.setOrderStatus(OrderStatus.PREPARING);
        assertEquals(order, orderDb);
    }

    @Test
    void takeOrderToDeliverThrowNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> deliveryService.takeOrderToDeliver(ID_NOT_EXISTING));
    }

    @Test
    void takeOrderToDeliverOrderNotWaitingDeliveryServiceThrowBadRequestException() {
        order.setOrderStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);

        assertThrows(BadRequestException.class, () -> deliveryService.takeOrderToDeliver(orderId));
    }

    @Test
    @Transactional
    void startDeliveryTest() {
        order.setOrderStatus(OrderStatus.PREPARING);
        orderRepository.save(order);

        deliveryService.startDelivery(order.getId());

        var orderDb = deliveryService.findById(order.getId());

        assertEquals(OrderStatus.DELIVERING, orderDb.getOrderStatus());

        order.setOrderStatus(OrderStatus.DELIVERING);
        assertEquals(order, orderDb);
    }

    @Test
    void startDeliveryThrowNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> deliveryService.startDelivery(ID_NOT_EXISTING));
    }

    @Test
    void startDeliveryOrderNotWaitingDeliveryServiceThrowBadRequestException() {
        order.setOrderStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);

        assertThrows(BadRequestException.class, () -> deliveryService.startDelivery(orderId));
    }

    @Test
    @Transactional
    void orderDeliveredTest() {
        order.setOrderStatus(OrderStatus.DELIVERING);
        orderRepository.save(order);

        deliveryService.orderDelivered(order.getId());

        var orderDb = deliveryService.findById(order.getId());

        assertEquals(OrderStatus.DELIVERED, orderDb.getOrderStatus());

        order.setOrderStatus(OrderStatus.DELIVERED);
        assertEquals(order, orderDb);
    }

    @Test
    void orderDeliveredThrowNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> deliveryService.orderDelivered(ID_NOT_EXISTING));
    }

    @Test
    void orderDeliveredOrderNotWaitingDeliveryServiceThrowBadRequestException() {
        order.setOrderStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);

        assertThrows(BadRequestException.class, () -> deliveryService.orderDelivered(orderId));
    }

    @Test
    void getSortedByPickupDistanceOrdersForDelivery() {
        var restaurant2 = Restaurant.builder()
                .name("Project 72")
                .address("Kosovska 15")
                .openingTime(LocalTime.of(10, 10))
                .closingTime(LocalTime.of(20, 20))
                .geolocation(new Geolocation(19.8493474, 45.2595586))
                .build();

        restaurant2 = restaurantRepository.save(restaurant2);

        var menuItem2 = MenuItem.builder()
                .name("Srneci cevapi")
                .price(BigDecimal.valueOf(500))
                .description("meat, cheese")
                .restaurant(restaurant2)
                .build();

        menuItem2 = menuItemRepository.save(menuItem2);

        var order2 = Order.builder()
                .orderTime(LocalDateTime.now(clock))
                .orderStatus(OrderStatus.CONFIRMED)
                .restaurant(restaurant2)
                .build();

        var orderItem2 = OrderItem.builder()
                .menuItem(menuItem2)
                .quantity(2)
                .additionalInfo("no ketchup")
                .order(order2)
                .build();

        order2.addOrderItem(orderItem2);
        order2.setOrderStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order2);

        var restaurant3 = Restaurant.builder()
                .name("Project 72")
                .address("Bulevar oslobodjenja 15")
                .openingTime(LocalTime.of(10, 10))
                .closingTime(LocalTime.of(20, 20))
                .geolocation(new Geolocation(19.8312667, 45.2624046))
                .build();

        restaurant3 = restaurantRepository.save(restaurant3);

        var menuItem3 = MenuItem.builder()
                .name("Srneci cevapi")
                .price(BigDecimal.valueOf(500))
                .description("meat, cheese")
                .restaurant(restaurant3)
                .build();

        menuItem3 = menuItemRepository.save(menuItem3);

        var order3 = Order.builder()
                .orderTime(LocalDateTime.now(clock))
                .orderStatus(OrderStatus.CONFIRMED)
                .restaurant(restaurant3)
                .build();

        var orderItem3 = OrderItem.builder()
                .menuItem(menuItem3)
                .quantity(2)
                .additionalInfo("no ketchup")
                .order(order3)
                .build();

        order3.addOrderItem(orderItem3);
        order3.setOrderStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order3);

        order.setOrderStatus(OrderStatus.CONFIRMED);
        order = orderRepository.save(order);

        var address = "Puskinova 6, Novi Sad";
        var ordersForDeliver = deliveryService.getSortedByPickupDistanceOrdersForDelivery(address);

        assertEquals(3, ordersForDeliver.size());
        assertTrue(ordersForDeliver.get(0).getRestaurantDistanceInMeters() < ordersForDeliver.get(1).getRestaurantDistanceInMeters());
        assertTrue(ordersForDeliver.get(1).getRestaurantDistanceInMeters() < ordersForDeliver.get(2).getRestaurantDistanceInMeters());
    }

}
