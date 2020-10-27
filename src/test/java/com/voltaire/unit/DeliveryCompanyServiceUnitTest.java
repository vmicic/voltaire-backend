package com.voltaire.unit;

import com.voltaire.delivery.DeliveryCompanyService;
import com.voltaire.delivery.model.DeliveryCompany;
import com.voltaire.delivery.repository.DeliveryCompanyRepository;
import com.voltaire.exception.customexceptions.BadRequestException;
import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.order.model.Order;
import com.voltaire.order.model.OrderItem;
import com.voltaire.order.model.OrderStatus;
import com.voltaire.order.repository.OrderRepository;
import com.voltaire.restaurant.model.MenuItem;
import com.voltaire.restaurant.model.Restaurant;
import com.voltaire.shared.GeocodeService;
import com.voltaire.shared.Geolocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "unit-test")
class DeliveryCompanyServiceUnitTest {

    private Integer confirmedOrderDeliveryTimeout;

    private final UUID ID_NOT_EXISTING = UUID.fromString("0fc3d32a-38c0-457c-b34f-8478fb92f3f4");
    private final UUID ORDER_ID = UUID.fromString("af49a717-0395-4ffd-8b11-fc3f9c02b565");

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private GeocodeService geocodeService;

    @Mock
    private DeliveryCompanyRepository deliveryCompanyRepository;

    @Mock
    private Clock clock;

    private Clock fixedClock;

    @InjectMocks
    private DeliveryCompanyService deliveryCompanyService;

    private Order order;

    private DeliveryCompany deliveryCompany;

    @BeforeEach
    public void setUp() {
        fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault());

        confirmedOrderDeliveryTimeout = 10;

        var restaurant = Restaurant.builder()
                .id(UUID.fromString("f9822d37-7357-4bd7-9ad7-e16b68da2e7c"))
                .name("My restaurant")
                .address("Brace Ribnikar 10")
                .openingTime(LocalTime.of(10, 10))
                .closingTime(LocalTime.of(20, 20))
                .geolocation(new Geolocation(19.8371365, 45.2479144))
                .build();

        var menuItem = MenuItem.builder()
                .id(UUID.fromString("bcbbf991-903e-4559-b893-62e5740078d0"))
                .name("Burger")
                .price(BigDecimal.valueOf(100.0))
                .description("ketchup, cheese")
                .build();

        this.order = Order.builder()
                .orderTime(LocalDateTime.now(fixedClock))
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

        this.deliveryCompany = DeliveryCompany.builder()
                .id(UUID.randomUUID())
                .name("Potrcko")
                .apiKey(UUID.randomUUID())
                .build();
    }

    @Test
    void getOrdersForDeliveryTest() {
        order.setId(ORDER_ID);
        ReflectionTestUtils.setField(deliveryCompanyService, "confirmedOrderDeliveryTimeout", confirmedOrderDeliveryTimeout);
        var timeCutoff = LocalDateTime.now(fixedClock).minusMinutes(confirmedOrderDeliveryTimeout);

        doReturn(fixedClock.instant()).when(clock).instant();
        doReturn(fixedClock.getZone()).when(clock).getZone();
        doReturn(List.of(order)).when(orderRepository).findAllByOrderTimeAfterAndOrderStatusEquals(timeCutoff, OrderStatus.CONFIRMED);

        var ordersForDelivery = deliveryCompanyService.getOrdersForDelivery();

        assertEquals(1, ordersForDelivery.size());
        verify(orderRepository).findAllByOrderTimeAfterAndOrderStatusEquals(timeCutoff, OrderStatus.CONFIRMED);
    }

    @Test
    void takeOrderToDeliverTest() {
        order.setId(ORDER_ID);
        order.setOrderStatus(OrderStatus.CONFIRMED);

        var orderExpectedToSave = order.toBuilder().build();
        orderExpectedToSave.setOrderStatus(OrderStatus.PREPARING);

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);
        doReturn(orderExpectedToSave).when(orderRepository).save(orderExpectedToSave);

        var idResponse = deliveryCompanyService.takeOrderToDeliver(ORDER_ID);

        assertEquals(ORDER_ID, idResponse.getId());
        verify(orderRepository).save(orderExpectedToSave);
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void takeOrderToDeliverThrowNotFoundException() {
        order.setId(ORDER_ID);

        doThrow(new EntityNotFoundException("id", ID_NOT_EXISTING.toString())).when(orderRepository).findById(ID_NOT_EXISTING);

        assertThrows(EntityNotFoundException.class, () -> deliveryCompanyService.takeOrderToDeliver(ID_NOT_EXISTING));
        verify(orderRepository).findById(ID_NOT_EXISTING);
    }

    @Test
    void takeOrderToDeliverOrderNotWaitingDeliveryServiceThrowBadRequestException() {
        order.setId(ORDER_ID);

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);

        assertThrows(BadRequestException.class, () -> deliveryCompanyService.takeOrderToDeliver(ORDER_ID));
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void startDeliveryTest() {
        order.setId(ORDER_ID);
        order.setOrderStatus(OrderStatus.PREPARING);

        var orderExpectedToSave = order.toBuilder().build();
        orderExpectedToSave.setOrderStatus(OrderStatus.DELIVERING);

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);
        doReturn(orderExpectedToSave).when(orderRepository).save(orderExpectedToSave);

        var idResponse = deliveryCompanyService.startDelivery(ORDER_ID);

        assertEquals(ORDER_ID, idResponse.getId());
        verify(orderRepository).save(orderExpectedToSave);
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void startDeliveryThrowNotFoundException() {
        order.setId(ORDER_ID);

        doThrow(new EntityNotFoundException("id", ID_NOT_EXISTING.toString())).when(orderRepository).findById(ID_NOT_EXISTING);

        assertThrows(EntityNotFoundException.class, () -> deliveryCompanyService.startDelivery(ID_NOT_EXISTING));
        verify(orderRepository).findById(ID_NOT_EXISTING);
    }

    @Test
    void startDeliveryOrderNotWaitingDeliveryServiceThrowBadRequestException() {
        order.setId(ORDER_ID);

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);

        assertThrows(BadRequestException.class, () -> deliveryCompanyService.startDelivery(ORDER_ID));
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void orderDeliveredTest() {
        order.setId(ORDER_ID);
        order.setOrderStatus(OrderStatus.DELIVERING);

        var orderExpectedToSave = order.toBuilder().build();
        orderExpectedToSave.setOrderStatus(OrderStatus.DELIVERED);

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);
        doReturn(orderExpectedToSave).when(orderRepository).save(orderExpectedToSave);

        var idResponse = deliveryCompanyService.orderDelivered(ORDER_ID);

        assertEquals(ORDER_ID, idResponse.getId());
        verify(orderRepository).save(orderExpectedToSave);
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void orderDeliveredThrowNotFoundException() {
        order.setId(ORDER_ID);

        doThrow(new EntityNotFoundException("id", ID_NOT_EXISTING.toString())).when(orderRepository).findById(ID_NOT_EXISTING);

        assertThrows(EntityNotFoundException.class, () -> deliveryCompanyService.orderDelivered(ID_NOT_EXISTING));
        verify(orderRepository).findById(ID_NOT_EXISTING);
    }

    @Test
    void orderDeliveredOrderNotWaitingDeliveryServiceThrowBadRequestException() {
        order.setId(ORDER_ID);

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);

        assertThrows(BadRequestException.class, () -> deliveryCompanyService.orderDelivered(ORDER_ID));
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void getSortedByPickupDistanceOrdersForDeliveryTest() {
        var address = "Brace Ribnikar 43, Novi Sad";
        var deliverymanPoint = new Geolocation(19.8340029, 45.2483411);

        var restaurant2 = Restaurant.builder()
                .id(UUID.fromString("f9822c37-7357-4bd7-9ad7-e16b68da2e7c"))
                .name("Project 72")
                .address("Kosovska 15")
                .openingTime(LocalTime.of(10, 10))
                .closingTime(LocalTime.of(20, 20))
                .geolocation(new Geolocation(19.8493474, 45.2595586))
                .build();

        var menuItem2 = MenuItem.builder()
                .id(UUID.fromString("bcbbf991-903e-4559-b893-62e5740078d0"))
                .name("Srneci cevapi")
                .price(BigDecimal.valueOf(500))
                .description("meat, cheese")
                .restaurant(restaurant2)
                .build();

        var order2 = Order.builder()
                .orderTime(LocalDateTime.now(fixedClock))
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

        this.order.setOrderStatus(OrderStatus.CONFIRMED);

        ReflectionTestUtils.setField(deliveryCompanyService, "confirmedOrderDeliveryTimeout", confirmedOrderDeliveryTimeout);
        var timeCutoff = LocalDateTime.now(fixedClock).minusMinutes(confirmedOrderDeliveryTimeout);

        doReturn(fixedClock.instant()).when(clock).instant();
        doReturn(fixedClock.getZone()).when(clock).getZone();
        doReturn(deliverymanPoint).when(geocodeService).getGeolocationForAddressString(address);
        doReturn(500.0).when(geocodeService).distance(order.getRestaurant().getGeolocation(), deliverymanPoint);
        doReturn(100.0).when(geocodeService).distance(order2.getRestaurant().getGeolocation(), deliverymanPoint);
        doReturn(List.of(order, order2)).when(orderRepository).findAllByOrderTimeAfterAndOrderStatusEquals(timeCutoff, OrderStatus.CONFIRMED);

        var ordersForDeliver = deliveryCompanyService.getSortedByPickupDistanceOrdersForDelivery(address);

        assertEquals(2, ordersForDeliver.size());
        assertTrue(ordersForDeliver.get(0).getRestaurantDistanceInMeters() < ordersForDeliver.get(1).getRestaurantDistanceInMeters());
        verify(geocodeService).getGeolocationForAddressString(address);
        verify(orderRepository).findAllByOrderTimeAfterAndOrderStatusEquals(timeCutoff, OrderStatus.CONFIRMED);
    }

    @Test
    void getApiKeyTest() {
        doReturn(Optional.of(deliveryCompany)).when(deliveryCompanyRepository).findById(deliveryCompany.getId());

        var apiKey = deliveryCompanyService.getApiKey(deliveryCompany.getId());

        assertEquals(deliveryCompany.getApiKey(), apiKey);
        verify(deliveryCompanyRepository).findById(deliveryCompany.getId());
    }

    @Test
    void generateNewApiKeyTest() {
        var oldApiKey = deliveryCompany.getApiKey();

        doReturn(Optional.of(deliveryCompany)).when(deliveryCompanyRepository).findById(deliveryCompany.getId());
        when(deliveryCompanyRepository.save(any(DeliveryCompany.class))).then(AdditionalAnswers.returnsFirstArg());

        var newApiKey = deliveryCompanyService.generateNewApiKey(deliveryCompany.getId());

        assertNotEquals(newApiKey, oldApiKey);
    }

    @Test
    void invalidApiKeyFalseTest() {
        doReturn(true).when(deliveryCompanyRepository).existsByApiKey(deliveryCompany.getApiKey());

        assertFalse(deliveryCompanyService.invalidApiKey(deliveryCompany.getApiKey()));
    }

    @Test
    void invalidApiKeyTrueTest() {
        doReturn(false).when(deliveryCompanyRepository).existsByApiKey(deliveryCompany.getApiKey());

        assertTrue(deliveryCompanyService.invalidApiKey(deliveryCompany.getApiKey()));
    }

}
