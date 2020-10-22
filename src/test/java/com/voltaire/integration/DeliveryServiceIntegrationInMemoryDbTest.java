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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

}
