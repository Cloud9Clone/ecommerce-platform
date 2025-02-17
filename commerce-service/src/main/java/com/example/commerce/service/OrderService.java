package com.example.commerce.service;

import com.example.commerce.dto.OrderRequestDTO;
import com.example.commerce.dto.OrderResponseDTO;
import com.example.commerce.model.Order;
import com.example.commerce.model.User;
import com.example.commerce.model.enums.OrderStatus;
import com.example.commerce.repository.OrderRepository;
import com.example.commerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderResponseDTO getOrderById(UUID orderId) {
        log.info("Retrieving order details for order ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order ID {} does not exist", orderId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
                });

        return mapToResponseDTO(order);
    }

    public List<OrderResponseDTO> getOrdersForUser(UUID userId, Optional<OrderStatus> status) {
        log.info("Retrieving orders for user ID: {} with status: {}", userId, status.orElse(null));

        if (!userRepository.existsById(userId)) {
            log.error("User with ID {} not found", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        List<Order> orders;
        if (status.isPresent()) {
            orders = orderRepository.findByUserUserIdAndStatus(userId, status.get());
        } else {
            orders = orderRepository.findByUserUserId(userId);
        }

        if (orders.isEmpty()) {
            log.warn("No order found for user ID: {} with status: {}", userId, status.orElse(null));
        }

        return orders.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public OrderResponseDTO createOrder(OrderRequestDTO requestDTO) {
        log.info("Creating a new order for user ID: {}", requestDTO.getUserId());

        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> {
                   log.error("User with user ID {} does not exist", requestDTO.getUserId());
                   return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                });

        Order order = new Order();
        order.setUser(user);
        order.setStreet(requestDTO.getStreet());
        order.setCity(requestDTO.getCity());
        order.setState(requestDTO.getState());
        order.setCountry(requestDTO.getCountry());
        order.setPostalCode(requestDTO.getPostalCode());
        order.setTotalPrice(requestDTO.getTotalPrice());
        order.setStatus(OrderStatus.valueOf(requestDTO.getStatus().toUpperCase()));

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getOrderId());

        return mapToResponseDTO(savedOrder);
    }

    public OrderResponseDTO updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        log.info("Updating order status for order ID: {} to {}", orderId, newStatus);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                   log.error("Updating non-existing order with ID: {}", orderId);
                   return new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
                });

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return mapToResponseDTO(updatedOrder);
    }

    public void deleteOrder(UUID orderId) {
        log.info("Deleting order with ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                   log.error("Order ID {} does not exist", orderId);
                   return new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
                });

        orderRepository.delete(order);
        log.info("Order with ID: {} deleted successfully", orderId);
    }

    private OrderResponseDTO mapToResponseDTO(Order order) {
        return new OrderResponseDTO(
            order.getOrderId(),
            order.getUser().getUserId(),
            order.getStreet(),
            order.getCity(),
            order.getState(),
            order.getCountry(),
            order.getPostalCode(),
            order.getTotalPrice(),
            order.getStatus().toString(),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }
}
