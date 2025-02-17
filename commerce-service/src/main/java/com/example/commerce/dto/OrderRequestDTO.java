package com.example.commerce.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotBlank(message = "Street is required")
    @Size(max = 255, message = "Street cannot exceed 255 characters")
    private String street;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State cannot exceed 100 characters")
    private String state;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country cannot exceed 100 characters")
    private String country;

    @NotBlank(message = "Postal code is required")
    @Size(max = 20, message = "Postal code cannot exceed 20 characters")
    @Pattern(regexp = "^\\d{4,5}$", message = "Postal code must be a valid 4 or 5-digit format")
    private String postalCode;

    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.01", message = "Total price must be greater than zero")
    private BigDecimal totalPrice;

    @NotBlank(message = "Order status is required")
    private String status;
}
