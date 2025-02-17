package com.example.commerce.service;

import com.example.commerce.dto.OrderItemRequestDTO;
import com.example.commerce.model.Order;
import com.example.commerce.model.OrderItem;
import com.example.commerce.dto.OrderItemResponseDTO;
import com.example.commerce.model.Product;
import com.example.commerce.repository.OrderItemRepository;
import com.example.commerce.repository.OrderRepository;
import com.example.commerce.repository.ProductRepository;
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
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public List<OrderItemResponseDTO> getOrderItemsByOrderId(UUID orderId) {
        log.info("Retrieving order items for order ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order with ID: {} not found", orderId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
                });

        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

        return orderItems.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public OrderItemResponseDTO addOrUpdateOrderItem(OrderItemRequestDTO requestDTO) {
        log.info("Processing order item for order ID: {} and product ID: {}", requestDTO.getOrderId(), requestDTO.getProductId());

        // Check if the order exists
        Order order = orderRepository.findById(requestDTO.getOrderId())
                .orElseThrow(() -> {
                    log.error("Order with ID: {} does not exist", requestDTO.getOrderId());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
                });

        // Check if the product exists
        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> {
                    log.error("Product with ID: {} does not exist", requestDTO.getProductId());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
                });

        if (product.getStock() < requestDTO.getQuantity()) {
            log.error("Insufficient stock for product ID: {}. Available: {}, Requested: {}", product.getProductId(), product.getStock(), requestDTO.getQuantity());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient stock for the product");
        }

        // Check if the product is already in the order
        Optional<OrderItem> existingOrderItem = orderItemRepository.findByOrderAndProduct(order, product);

        OrderItem orderItem;
        if (existingOrderItem.isPresent()) {
            orderItem = existingOrderItem.get();
            int newQuantity = orderItem.getQuantity() + requestDTO.getQuantity();

            if (product.getStock() < newQuantity) {
                log.error("Not enough stock for product ID: {} to update quantity. Available: {}, Required: {}", product.getProductId(), product.getStock(), newQuantity);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient stock for the updated quantity");
            }

            orderItem.setQuantity(newQuantity);
            log.info("Updated quantity for order item with product ID: {} to {}", product.getProductId(), newQuantity);
        } else {
            orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(requestDTO.getQuantity());
            orderItem.setPrice(requestDTO.getPrice());

            log.info("Created new order item for product ID: {} with quantity: {}", product.getProductId(), requestDTO.getQuantity());
        }

        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        log.info("Successfully processed order item for product ID: {}", product.getProductId());

        return mapToResponseDTO(savedOrderItem);
    }

    public void deleteOrderItem(UUID orderItemId) {
        log.info("Removing order item with ID: {}", orderItemId);

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> {
                    log.error("Order item with ID: {} not found", orderItemId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Order item not found");
                });

        orderItemRepository.delete(orderItem);
        log.info("Order item with ID: {} removed", orderItemId);
    }

    private OrderItemResponseDTO mapToResponseDTO(OrderItem orderItem) {
        return new OrderItemResponseDTO(
                orderItem.getOrderItemId(),
                orderItem.getOrder().getOrderId(),
                orderItem.getProduct().getProductId(),
                orderItem.getQuantity(),
                orderItem.getPrice(),
                orderItem.getProduct().getName(),
                orderItem.getCreatedAt(),
                orderItem.getUpdatedAt()
        );
    }
}
