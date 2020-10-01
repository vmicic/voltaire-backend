package com.voltaire.order;

import com.voltaire.restaurant.MenuItemService;
import com.voltaire.restaurant.RestaurantService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantService restaurantService;
    private final MenuItemService menuItemService;

    public OrderServiceImpl(OrderRepository orderRepository, RestaurantService restaurantService, MenuItemService menuItemService) {
        this.orderRepository = orderRepository;
        this.restaurantService = restaurantService;
        this.menuItemService = menuItemService;
    }

    @Override
    public Order createOrder(OrderDTO orderDTO) {
        Order order = new OrderBuilder()
                .setOrderTime(LocalDateTime.now())
                .setOrderStatus(OrderStatus.CREATED)
                .setRestaurant(restaurantService.findById(orderDTO.getRestaurantId()))
                .build();
        createOrderItems(orderDTO.getOrderItems(), order);

        return orderRepository.save(order);
    }

    public void createOrderItems(List<OrderItemDTO> orderItemDTOs, Order order) {
        orderItemDTOs.stream().forEach(orderItemDTO -> {
            OrderItem orderItem = new OrderItemBuilder()
                    .setMenuItem(menuItemService.findById(orderItemDTO.getMenuItemId()))
                    .setOrder(order)
                    .setQuantity(orderItemDTO.getQuantity())
                    .setAdditionalInfo(orderItemDTO.getAdditionalInfo())
                    .build();

            order.addOrderItem(orderItem);
        });
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public boolean notExists(Long id) {
        return !orderRepository.existsById(id);
    }

    @Override
    public boolean notWaitingConfirmOrReject(Long id) {
        Order order = this.findById(id);

        return !order.getOrderStatus().equals(OrderStatus.CREATED);
    }

    @Override
    public void confirmOrder(Long id) {
        Order order = this.findById(id);

        order.setOrderStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
    }

    @Override
    public void rejectOrder(Long id) {
        Order order = this.findById(id);

        order.setOrderStatus(OrderStatus.REJECTED);
        orderRepository.save(order);
    }
}
