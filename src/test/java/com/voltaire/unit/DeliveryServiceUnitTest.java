package com.voltaire.unit;

import com.voltaire.delivery.DeliveryService;
import com.voltaire.exception.customexceptions.BadRequestException;
import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.order.model.Order;
import com.voltaire.order.model.OrderItem;
import com.voltaire.order.model.OrderStatus;
import com.voltaire.order.repository.OrderRepository;
import com.voltaire.restaurant.model.MenuItem;
import com.voltaire.restaurant.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "unit-test")
class DeliveryServiceUnitTest {

    private Integer confirmedOrderDeliveryTimeout;

    private final UUID ID_NOT_EXISTING = UUID.fromString("0fc3d32a-38c0-457c-b34f-8478fb92f3f4");
    private final UUID ORDER_ID = UUID.fromString("af49a717-0395-4ffd-8b11-fc3f9c02b565");

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private Clock clock;

    private Clock fixedClock;

    @InjectMocks
    private DeliveryService deliveryService;

    private Order order;

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
    }

    @Test
    void getOrdersForDeliveryTest() {
        order.setId(ORDER_ID);
        ReflectionTestUtils.setField(deliveryService, "confirmedOrderDeliveryTimeout", confirmedOrderDeliveryTimeout);
        var timeCutoff = LocalDateTime.now(fixedClock).minusMinutes(confirmedOrderDeliveryTimeout);

        doReturn(fixedClock.instant()).when(clock).instant();
        doReturn(fixedClock.getZone()).when(clock).getZone();
        doReturn(List.of(order)).when(orderRepository).findAllByOrderTimeAfterAndOrderStatusEquals(timeCutoff, OrderStatus.CONFIRMED);

        var ordersForDelivery = deliveryService.getOrdersForDelivery();

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

        var idResponse = deliveryService.takeOrderToDeliver(ORDER_ID);

        assertEquals(ORDER_ID, idResponse.getId());
        verify(orderRepository).save(orderExpectedToSave);
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void takeOrderToDeliverThrowNotFoundException() {
        order.setId(ORDER_ID);

        doThrow(new EntityNotFoundException("id", ID_NOT_EXISTING.toString())).when(orderRepository).findById(ID_NOT_EXISTING);

        assertThrows(EntityNotFoundException.class, () -> deliveryService.takeOrderToDeliver(ID_NOT_EXISTING));
        verify(orderRepository).findById(ID_NOT_EXISTING);
    }

    @Test
    void takeOrderToDeliverOrderNotWaitingDeliveryServiceThrowBadRequestException() {
        order.setId(ORDER_ID);

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);

        assertThrows(BadRequestException.class, () -> deliveryService.takeOrderToDeliver(ORDER_ID));
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

        var idResponse = deliveryService.startDelivery(ORDER_ID);

        assertEquals(ORDER_ID, idResponse.getId());
        verify(orderRepository).save(orderExpectedToSave);
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void startDeliveryThrowNotFoundException() {
        order.setId(ORDER_ID);

        doThrow(new EntityNotFoundException("id", ID_NOT_EXISTING.toString())).when(orderRepository).findById(ID_NOT_EXISTING);

        assertThrows(EntityNotFoundException.class, () -> deliveryService.startDelivery(ID_NOT_EXISTING));
        verify(orderRepository).findById(ID_NOT_EXISTING);
    }

    @Test
    void startDeliveryOrderNotWaitingDeliveryServiceThrowBadRequestException() {
        order.setId(ORDER_ID);

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);

        assertThrows(BadRequestException.class, () -> deliveryService.startDelivery(ORDER_ID));
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

        var idResponse = deliveryService.orderDelivered(ORDER_ID);

        assertEquals(ORDER_ID, idResponse.getId());
        verify(orderRepository).save(orderExpectedToSave);
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void orderDeliveredThrowNotFoundException() {
        order.setId(ORDER_ID);

        doThrow(new EntityNotFoundException("id", ID_NOT_EXISTING.toString())).when(orderRepository).findById(ID_NOT_EXISTING);

        assertThrows(EntityNotFoundException.class, () -> deliveryService.orderDelivered(ID_NOT_EXISTING));
        verify(orderRepository).findById(ID_NOT_EXISTING);
    }

    @Test
    void orderDeliveredOrderNotWaitingDeliveryServiceThrowBadRequestException() {
        order.setId(ORDER_ID);

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);

        assertThrows(BadRequestException.class, () -> deliveryService.orderDelivered(ORDER_ID));
        verify(orderRepository).findById(ORDER_ID);
    }
}
