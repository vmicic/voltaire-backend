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
import org.springframework.test.context.ActiveProfiles;

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

    private Integer confirmedOrderDeliveryTimeout;

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

        confirmedOrderDeliveryTimeout = 10;

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

        var orderItems = new ArrayList<OrderItem>();
        this.order.addOrderItem(orderItem);

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
    void createOrderTest() {
        doReturn(Optional.of(restaurant)).when(restaurantRepository).findById(restaurant.getId());
        doReturn(Optional.of(menuItem)).when(menuItemRepository).findById(menuItem.getId());
        doReturn(order).when(orderRepository).save(order);
        doReturn(fixedClock.instant()).when(clock).instant();
        doReturn(fixedClock.getZone()).when(clock).getZone();

        var createdOrder = orderService.createOrder(createOrderRequest);

        assertEquals(order, createdOrder);
        verify(orderRepository).save(order);
        verify(restaurantRepository).findById(restaurant.getId());
        verify(menuItemRepository).findById(menuItem.getId());
    }

    @Test
    void createOrderThrowNotFoundExceptionForRestaurant() {
        doThrow(new EntityNotFoundException("id", restaurant.getId().toString())).when(restaurantRepository).findById(restaurant.getId());
        doReturn(fixedClock.instant()).when(clock).instant();
        doReturn(fixedClock.getZone()).when(clock).getZone();

        assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(createOrderRequest));
    }

    @Test
    void createOrderThrowNotFoundExceptionForMenuItem() {
        doReturn(Optional.of(restaurant)).when(restaurantRepository).findById(restaurant.getId());
        doThrow(new EntityNotFoundException("id", menuItem.getId().toString())).when(menuItemRepository).findById(menuItem.getId());
        doReturn(fixedClock.instant()).when(clock).instant();
        doReturn(fixedClock.getZone()).when(clock).getZone();

        assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(createOrderRequest));
    }

    @Test
    void confirmOrderTest() {
        order.setId(ORDER_ID);

        var expectedOrderToSave = order.toBuilder().build();
        var preparationMinutes = 15;

        expectedOrderToSave.setOrderStatus(OrderStatus.CONFIRMED);
        expectedOrderToSave.setMinutesForPreparation(preparationMinutes);

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);
        doReturn(expectedOrderToSave).when(orderRepository).save(expectedOrderToSave);

        var idResponse = orderService.confirmOrder(ORDER_ID, preparationMinutes);

        assertEquals(order.getId(), idResponse.getId());
        verify(orderRepository).findById(order.getId());
        verify(orderRepository).save(expectedOrderToSave);
    }

    @Test
    void confirmOrderNotWaitingConfirmationThrowBadRequestException() {
        order.setId(ORDER_ID);
        order.setOrderStatus(OrderStatus.CONFIRMED);

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);

        assertThrows(BadRequestException.class, () -> orderService.confirmOrder(ORDER_ID, 15));
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void confirmOrderThrowNotFoundExceptionForOrder() {
        order.setId(ORDER_ID);

        doThrow(new EntityNotFoundException("id", ID_NOT_EXISTING.toString())).when(orderRepository).findById(ID_NOT_EXISTING);

        assertThrows(EntityNotFoundException.class, () -> orderService.confirmOrder(ID_NOT_EXISTING, 15));
        verify(orderRepository).findById(ID_NOT_EXISTING);
    }

    @Test
    void rejectOrderTest() {
        order.setId(ORDER_ID);

        var orderExpectedToSave = order.toBuilder().build();

        orderExpectedToSave.setOrderStatus(OrderStatus.REJECTED);

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);
        doReturn(orderExpectedToSave).when(orderRepository).save(orderExpectedToSave);

        var idResponse = orderService.rejectOrder(ORDER_ID);

        assertEquals(order.getId(), idResponse.getId());
        verify(orderRepository).findById(order.getId());
        verify(orderRepository).save(orderExpectedToSave);
    }

    @Test
    void rejectOrderNotWaitingConfirmationThrowBadRequestException() {
        order.setId(ORDER_ID);
        order.setOrderStatus(OrderStatus.DELIVERING);

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);

        assertThrows(BadRequestException.class, () -> orderService.rejectOrder(ORDER_ID));
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void rejectOrderThrowNotFoundExceptionForOrder() {
        order.setId(ORDER_ID);

        doThrow(new EntityNotFoundException("id", ID_NOT_EXISTING.toString())).when(orderRepository).findById(ID_NOT_EXISTING);

        assertThrows(EntityNotFoundException.class, () -> orderService.rejectOrder(ID_NOT_EXISTING));
        verify(orderRepository).findById(ID_NOT_EXISTING);
    }

    @Test
    void findByIdTest() {
        order.setId(ORDER_ID);

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);

        var foundOrder = orderService.findById(ORDER_ID);

        assertEquals(order, foundOrder);
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void findByIdThrowNotFoundException() {
        order.setId(ORDER_ID);

        doThrow(new EntityNotFoundException("id", ID_NOT_EXISTING.toString())).when(orderRepository).findById(ID_NOT_EXISTING);

        assertThrows(EntityNotFoundException.class, () -> orderService.findById(ID_NOT_EXISTING));
        verify(orderRepository).findById(ID_NOT_EXISTING);
    }


}
