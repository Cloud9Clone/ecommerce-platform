package com.example.commerce.model.enums;

public enum PaymentStatus {
    PENDING,    // Payment data has not been provided or payment is not yet processed
    COMPLETED,  // Payment data is provided, and the payment is successful
    FAILED,     // Payment attempt failed (for later integration with real payment gateways)
    CANCELLED   // Payment was canceled by the user or due to inactivity
}
