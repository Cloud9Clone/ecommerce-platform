package com.example.commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderItemResponseDTO {

    private UUID orderItemId;
    private UUID orderId;
    private UUID productId;
    private Integer quantity;
    private BigDecimal price;
    private String productName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
