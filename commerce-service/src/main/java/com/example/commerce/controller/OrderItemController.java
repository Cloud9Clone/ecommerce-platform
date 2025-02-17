package com.example.commerce.controller;

import com.example.commerce.dto.OrderItemRequestDTO;
import com.example.commerce.dto.OrderItemResponseDTO;
import com.example.commerce.service.OrderItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping("/{orderId}")
    public ResponseEntity<List<OrderItemResponseDTO>> getOrderItemsByOrderId(@PathVariable UUID orderId) {
        log.info("Retrieving order items for order ID: {}", orderId);
        List<OrderItemResponseDTO> orderItems = orderItemService.getOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(orderItems);
    }

    @PostMapping
    public ResponseEntity<OrderItemResponseDTO> addOrUpdateOrderItem(@Valid @RequestBody OrderItemRequestDTO requestDTO) {
        log.info("Adding or updating order item for order ID: {}", requestDTO.getOrderId());
        OrderItemResponseDTO responseDTO = orderItemService.addOrUpdateOrderItem(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @DeleteMapping("/{orderItemId}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable UUID orderItemId) {
        log.info("Deleting order item with ID: {}", orderItemId);
        orderItemService.deleteOrderItem(orderItemId);
        return ResponseEntity.noContent().build();
    }
}
