package com.example.commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddressResponseDTO {

    private UUID addressId;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private LocalDateTime createdAt;
}
