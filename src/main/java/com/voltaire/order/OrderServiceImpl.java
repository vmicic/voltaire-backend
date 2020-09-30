package com.voltaire.order;

import com.voltaire.restaurant.MenuItem;
import com.voltaire.restaurant.MenuItemService;
import com.voltaire.restaurant.Restaurant;
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
        Order order = new Order();

        Restaurant restaurant = restaurantService.findById(orderDTO.getRestaurantId());
        order.setRestaurant(restaurant);
        order.setLocalDateTime(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.CREATED);

        for(OrderItemDTO orderItemDTO : orderDTO.getOrderItems()) {
            OrderItem orderItem = new OrderItem();

            MenuItem menuItem = menuItemService.findById(orderItemDTO.getMenuItemId());
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(orderItemDTO.getQuantity());
            orderItem.setAdditionalInfo(orderItemDTO.getAdditionalInfo());
            orderItem.setOrder(order);
            order.addOrderItem(orderItem);
        }

        return orderRepository.save(order);
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
