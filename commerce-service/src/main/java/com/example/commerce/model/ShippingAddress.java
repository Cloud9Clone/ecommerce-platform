package com.example.commerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shipping_addresses", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "street", "city", "postal_code"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddress {

    @Id
    @GeneratedValue
    @Column(name = "address_id")
    private UUID addressId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Street is required")
    @Size(max = 255, message = "Street cannot exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String street;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String state;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String country;

    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "^\\d{4,5}$", message = "Postal code must be a valid 4 or 5-digit format")
    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
