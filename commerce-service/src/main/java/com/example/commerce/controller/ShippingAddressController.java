package com.example.commerce.controller;

import com.example.commerce.dto.ShippingAddressRequestDTO;
import com.example.commerce.dto.ShippingAddressResponseDTO;
import com.example.commerce.service.ShippingAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/users/{userId}/addresses")
@RequiredArgsConstructor
public class ShippingAddressController {

    private final ShippingAddressService shippingAddressService;

    @GetMapping
    public ResponseEntity<List<ShippingAddressResponseDTO>> getShippingAddressesForUser(@PathVariable UUID userId) {
        log.info("Received request to get all shipping addresses for user ID: {}", userId);
        List<ShippingAddressResponseDTO> addresses = shippingAddressService.getShippingAddressesForUser(userId);
        log.info("Successfully retrieved {} shipping addresses for user ID: {}", addresses.size(), userId);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<ShippingAddressResponseDTO> getShippingAddressById(@PathVariable UUID userId, @PathVariable UUID addressId) {
        log.info("Received request to get a shipping address for address ID: {}", addressId);
        ShippingAddressResponseDTO responseDTO = shippingAddressService.getShippingAddressById(addressId);
        log.info("Successfully retrieved shipping address");
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    public ResponseEntity<ShippingAddressResponseDTO> createShippingAddress(@PathVariable UUID userId, @Valid @RequestBody ShippingAddressRequestDTO requestDTO) {
        log.info("Received request to create shipping address for user ID: {}", userId);
        ShippingAddressResponseDTO responseDTO = shippingAddressService.createShippingAddress(userId, requestDTO);
        log.info("Successfully created shipping address for user ID: {}", userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<ShippingAddressResponseDTO> updateShippingAddress(@PathVariable UUID userId, @PathVariable UUID addressId, @Valid @RequestBody ShippingAddressRequestDTO requestDTO) {
        log.info("Received request to update shipping address with ID: {} for user ID: {}", addressId, userId);
        ShippingAddressResponseDTO updatedAddress = shippingAddressService.updateShippingAddress(addressId, requestDTO);
        log.info("Successfully updated shipping address with ID: {} for user ID: {}", addressId, userId);
        return ResponseEntity.ok(updatedAddress);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteShippingAddress(@PathVariable UUID userId, @PathVariable UUID addressId) {
        log.info("Received request to delete shipping address with ID: {} for user ID: {}", addressId, userId);
        shippingAddressService.deleteShippingAddress(addressId);
        log.info("Successfully deleted shipping address with ID: {}", addressId);
        return ResponseEntity.noContent().build();
    }
}
