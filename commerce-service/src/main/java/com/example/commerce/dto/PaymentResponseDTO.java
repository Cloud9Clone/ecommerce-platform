package com.example.commerce.dto;

import com.example.commerce.model.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {

    private UUID paymentId;
    private UUID orderId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String status;
    private String transactionId; // Transaction ID from the external payment provider
    private LocalDateTime createdAt;
}
