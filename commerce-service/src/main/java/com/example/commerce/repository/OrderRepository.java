package com.example.commerce.repository;

import com.example.commerce.model.Order;
import com.example.commerce.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserUserId(UUID userId);
    List<Order> findByUserUserIdAndStatus(UUID userId, OrderStatus status);
}
