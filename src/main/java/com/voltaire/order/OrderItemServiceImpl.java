package com.voltaire.order;

import com.voltaire.restaurant.MenuItem;
import com.voltaire.restaurant.MenuItemService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final MenuItemService menuItemService;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, MenuItemService menuItemService) {
        this.orderItemRepository = orderItemRepository;
        this.menuItemService = menuItemService;
    }

    @Override
    public List<OrderItem> saveAll(Order order, List<OrderItemDTO> orderItemDTOS) {
        List<OrderItem> orderItems = new ArrayList<>();

        for(OrderItemDTO orderItemDTO : orderItemDTOS) {
            OrderItem orderItem = new OrderItem();

            MenuItem menuItem = menuItemService.findById(orderItemDTO.getMenuItemId());
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(orderItemDTO.getQuantity());
            orderItem.setAdditionalInfo(orderItemDTO.getAdditionalInfo());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }

        return orderItemRepository.saveAll(orderItems);
    }
}
