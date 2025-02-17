package com.example.commerce.controller;

import com.example.commerce.dto.OrderRequestDTO;
import com.example.commerce.dto.OrderResponseDTO;
import com.example.commerce.model.enums.OrderStatus;
import com.example.commerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable UUID orderId) {
        log.info("Received request to get order with ID: {}", orderId);
        OrderResponseDTO responseDTO = orderService.getOrderById(orderId);
        log.info("Successfully retrieved order with ID: {}", orderId);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersForUser(@PathVariable UUID userId, @RequestParam(required = false) OrderStatus orderStatus) {
        log.info("Received request to get orders for user ID: {} with status: {}", userId, orderStatus);
        List<OrderResponseDTO> orders = orderService.getOrdersForUser(userId, Optional.ofNullable(orderStatus));

        if (orders.isEmpty()) {
            String message = orderStatus != null
                    ? String.format("No orders found for user ID %s with status %s", userId, orderStatus)
                    : String.format("No orders found for user ID %s", userId);
            log.warn(message);
            return ResponseEntity.ok(Collections.emptyList());
        }

        log.info("Successfully retrieved {} orders for user ID: {}", orders.size(), userId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO requestDTO) {
        log.info("Received request to create a new order for user ID: {}", requestDTO.getUserId());
        OrderResponseDTO responseDTO = orderService.createOrder(requestDTO);
        log.info("Successfully created order with ID: {}", responseDTO.getOrderId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(@PathVariable UUID orderId, @RequestParam OrderStatus newStatus) {
        log.info("Received request to update order status for order ID: {} to {}", orderId, newStatus);
        OrderResponseDTO responseDTO = orderService.updateOrderStatus(orderId, newStatus);
        log.info("Successfully updated order status for order ID: {} to {}", orderId, responseDTO.getStatus());
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID orderId) {
        log.info("Received request to delete order with ID: {}", orderId);
        orderService.deleteOrder(orderId);
        log.info("Successfully deleted order with ID: {}", orderId);
        return ResponseEntity.noContent().build();
    }
}
