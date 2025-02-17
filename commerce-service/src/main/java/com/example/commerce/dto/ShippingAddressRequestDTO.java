package com.example.commerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddressRequestDTO {

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
}
