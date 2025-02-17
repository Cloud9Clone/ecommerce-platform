package com.example.commerce.model.enums;

public enum OrderStatus {
    PENDING,    // Order is created but yet finalized or paid
    COMPLETED,  // Payment is successful, and the order is finalized
    CANCELLED   // Order is cancelled (can be added in the future)
}
