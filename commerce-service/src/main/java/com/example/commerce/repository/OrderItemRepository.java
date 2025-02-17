package com.example.commerce.repository;

import com.example.commerce.model.Order;
import com.example.commerce.model.OrderItem;
import com.example.commerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    List<OrderItem> findByOrderOrderId(UUID orderId);
    List<OrderItem> findByProductProductId(UUID productId);
    Optional<OrderItem> findByOrderAndProduct(Order order, Product product);

    List<OrderItem> findByOrder(Order order);
}
