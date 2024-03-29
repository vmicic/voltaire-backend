package com.voltaire.order.repository;

import com.voltaire.order.model.Order;
import com.voltaire.order.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findAllByOrderTimeAfterAndOrderStatusEquals(LocalDateTime time, OrderStatus orderStatus);
}
