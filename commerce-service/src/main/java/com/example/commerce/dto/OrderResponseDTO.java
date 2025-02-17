package com.example.commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private UUID orderId;
    private UUID userId;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
