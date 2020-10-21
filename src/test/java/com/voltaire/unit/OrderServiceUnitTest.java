package com.voltaire.unit;

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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "unit-test")
class OrderServiceUnitTest {

    private Integer maxMinutesAgo;

    @Value("${hehe}")
    private String hehe;

    private final UUID ID_NOT_EXISTING = UUID.fromString("0fc3d32a-38c0-457c-b34f-8478fb92f3f4");
    private final UUID ORDER_ID = UUID.fromString("af49a717-0395-4ffd-8b11-fc3f9c02b565");

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private Clock clock;

    private Clock fixedClock;

    @InjectMocks
    private OrderService orderService;

    private Restaurant restaurant;
    private MenuItem menuItem;
    private Order order;
    private CreateOrderRequest createOrderRequest;

    @BeforeEach
    public void setUp() {
        fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault());

        maxMinutesAgo = 10;

         this.restaurant = Restaurant.builder()
                .id(UUID.fromString("f9822d37-7357-4bd7-9ad7-e16b68da2e7c"))
                .name("My restaurant")
                .address("Brace Ribnikar 10")
                .openingTime(LocalTime.of(10, 10))
                .closingTime(LocalTime.of(20, 20))
                .build();

        this.menuItem = MenuItem.builder()
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

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        this.order.setOrderItems(orderItems);

        var createOrderItemRequest = CreateOrderItemRequest.builder()
                .menuItemId(menuItem.getId())
                .quantity(orderItem.getQuantity())
                .additionalInfo(orderItem.getAdditionalInfo())
                .build();

        List<CreateOrderItemRequest> createOrderItemRequests = List.of(createOrderItemRequest);

        this.createOrderRequest = CreateOrderRequest.builder()
                .restaurantId(restaurant.getId())
                .orderItems(createOrderItemRequests)
                .build();
    }

    @Test
    void createOrderTest() {
        when(restaurantRepository.findById(restaurant.getId())).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.findById(menuItem.getId())).thenReturn(Optional.of(menuItem));
        when(orderRepository.save(order)).thenReturn(order);
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());

        var newOrder = orderService.createOrder(createOrderRequest);

        assertEquals(order, newOrder);
        verify(orderRepository).save(order);
        verify(restaurantRepository).findById(restaurant.getId());
        verify(menuItemRepository).findById(menuItem.getId());
    }

    @Test
    void createOrderThrowNotFoundExceptionForRestaurant() {
        when(restaurantRepository.findById(restaurant.getId())).thenThrow(new EntityNotFoundException("id", restaurant.getId().toString()));
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());

        assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(createOrderRequest));
    }

    @Test
    void createOrderThrowNotFoundExceptionForMenuItem() {
        when(restaurantRepository.findById(restaurant.getId())).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.findById(menuItem.getId())).thenThrow(new EntityNotFoundException("id", menuItem.getId().toString()));
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());

        assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(createOrderRequest));
    }

    @Test
    void confirmOrderTest() {
        order.setId(ORDER_ID);

        var expectedOrderToSave = order.toBuilder().build();
        var preparationMinutes = 15;

        expectedOrderToSave.setOrderStatus(OrderStatus.CONFIRMED);
        expectedOrderToSave.setPreparationMinutes(preparationMinutes);

        lenient().when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        lenient().when(orderRepository.save(expectedOrderToSave)).thenReturn(expectedOrderToSave);

        var idResponse = orderService.confirmOrder(ORDER_ID, preparationMinutes);

        assertEquals(order.getId(), idResponse.getId());
        verify(orderRepository).findById(order.getId());
        verify(orderRepository).save(expectedOrderToSave);
    }

    @Test
    void confirmOrderNotWaitingConfirmationThrowBadRequestException() {
        order.setId(ORDER_ID);
        order.setOrderStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        assertThrows(BadRequestException.class, () -> orderService.confirmOrder(ORDER_ID, 15));
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void confirmOrderThrowNotFoundExceptionForOrder() {
        order.setId(ORDER_ID);

        when(orderRepository.findById(ORDER_ID)).thenThrow(new EntityNotFoundException("id", ORDER_ID.toString()));

        assertThrows(EntityNotFoundException.class, () -> orderService.confirmOrder(ORDER_ID, 15));
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void rejectOrderTest() {
        order.setId(ORDER_ID);

        var orderExpectedToSave = order.toBuilder().build();

        orderExpectedToSave.setOrderStatus(OrderStatus.REJECTED);

        lenient().when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        lenient().when(orderRepository.save(orderExpectedToSave)).thenReturn(orderExpectedToSave);

        var idResponse = orderService.rejectOrder(ORDER_ID);

        assertEquals(order.getId(), idResponse.getId());
        verify(orderRepository).findById(order.getId());
        verify(orderRepository).save(orderExpectedToSave);
    }

    @Test
    void rejectOrderNotWaitingConfirmationThrowBadRequestException() {
        order.setId(ORDER_ID);
        order.setOrderStatus(OrderStatus.DELIVERING);

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        assertThrows(BadRequestException.class, () -> orderService.rejectOrder(ORDER_ID));
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void rejectOrderThrowNotFoundExceptionForOrder() {
        order.setId(ORDER_ID);

        when(orderRepository.findById(ORDER_ID)).thenThrow(new EntityNotFoundException("id", ORDER_ID.toString()));

        assertThrows(EntityNotFoundException.class, () -> orderService.rejectOrder(ORDER_ID));
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void findByIdTest() {
        order.setId(ORDER_ID);

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        var foundOrder = orderService.findById(ORDER_ID);

        assertEquals(order, foundOrder);
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void findByIdThrowNotFoundException() {
        order.setId(ORDER_ID);

        when(orderRepository.findById(ORDER_ID)).thenThrow(new EntityNotFoundException("id", ORDER_ID.toString()));

        assertThrows(EntityNotFoundException.class, () -> orderService.findById(ORDER_ID));
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void getOrdersForDeliveryTest() {
        order.setId(ORDER_ID);
        ReflectionTestUtils.setField(orderService, "maxMinutesAgo", maxMinutesAgo);
        var timeCutoff = LocalDateTime.now(fixedClock).minusMinutes(maxMinutesAgo);

        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());
        when(orderRepository.findAllByOrderTimeAfterAndOrderStatusEquals(timeCutoff, OrderStatus.CONFIRMED)).thenReturn(List.of(order));

        var ordersForDelivery = orderService.getOrdersForDelivery();

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

        var idResponse = orderService.takeOrderToDeliver(ORDER_ID);

        assertEquals(ORDER_ID, idResponse.getId());
        verify(orderRepository).save(orderExpectedToSave);
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void takeOrderToDeliverThrowNotFoundException() {
        order.setId(ORDER_ID);

        doThrow(new EntityNotFoundException("id", ORDER_ID.toString())).when(orderRepository).findById(ORDER_ID);

        assertThrows(EntityNotFoundException.class, () -> orderService.takeOrderToDeliver(ORDER_ID));
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void takeOrderToDeliverOrderNotWaitingDeliveryServiceThrowBadRequestException() {
        order.setId(ORDER_ID);

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);

        assertThrows(BadRequestException.class, () -> orderService.takeOrderToDeliver(ORDER_ID));
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

        var idResponse = orderService.startDelivery(ORDER_ID);

        assertEquals(ORDER_ID, idResponse.getId());
        verify(orderRepository).save(orderExpectedToSave);
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void startDeliveryThrowNotFoundException() {
        order.setId(ORDER_ID);

        doThrow(new EntityNotFoundException("id", ORDER_ID.toString())).when(orderRepository).findById(ORDER_ID);

        assertThrows(EntityNotFoundException.class, () -> orderService.startDelivery(ORDER_ID));
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void startDeliveryOrderNotWaitingDeliveryServiceThrowBadRequestException() {
        order.setId(ORDER_ID);

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);

        assertThrows(BadRequestException.class, () -> orderService.startDelivery(ORDER_ID));
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

        var idResponse = orderService.orderDelivered(ORDER_ID);

        assertEquals(ORDER_ID, idResponse.getId());
        verify(orderRepository).save(orderExpectedToSave);
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void orderDeliveredThrowNotFoundException() {
        order.setId(ORDER_ID);

        doThrow(new EntityNotFoundException("id", ORDER_ID.toString())).when(orderRepository).findById(ORDER_ID);

        assertThrows(EntityNotFoundException.class, () -> orderService.orderDelivered(ORDER_ID));
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void orderDeliveredOrderNotWaitingDeliveryServiceThrowBadRequestException() {
        order.setId(ORDER_ID);

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);

        assertThrows(BadRequestException.class, () -> orderService.orderDelivered(ORDER_ID));
        verify(orderRepository).findById(ORDER_ID);
    }

}
