package com.voltaire.order;

import java.util.List;

public interface OrderItemService {

    List<OrderItem> saveAll(Order order, List<OrderItemDTO> orderItemDTOS);
}
