package com.voltaire.order;

import java.util.List;

public interface OrderService {

    Order createOrder(OrderDTO orderDTO);

    List<Order> findAll();

    Order findById(Long id);

    boolean notExists(Long id);
}
