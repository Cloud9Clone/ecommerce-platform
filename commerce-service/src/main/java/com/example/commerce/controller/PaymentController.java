package com.example.commerce.controller;

import com.example.commerce.dto.PaymentRequestDTO;
import com.example.commerce.dto.PaymentResponseDTO;
import com.example.commerce.model.enums.PaymentStatus;
import com.example.commerce.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable UUID paymentId) {
        log.info("Received request to a payment with ID: {}", paymentId);
        PaymentResponseDTO responseDTO = paymentService.getPaymentById(paymentId);
        log.info("Successfully retrieved payment with ID: {}", paymentId);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(@Valid @RequestBody PaymentRequestDTO requestDTO) {
        log.info("Received request to create payment for order ID: {}", requestDTO.getOrderId());
        PaymentResponseDTO responseDTO = paymentService.createPayment(requestDTO);
        log.info("Successfully created payment with ID: {}", responseDTO.getPaymentId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/{paymentId}/status")
    public ResponseEntity<PaymentResponseDTO> updatePayment(@PathVariable UUID paymentId, @RequestParam PaymentStatus newStatus) {
        log.info("Received request to update payment with ID: {}", paymentId);
        PaymentResponseDTO responseDTO = paymentService.updatePaymentStatus(paymentId, newStatus);
        log.info("Successfully updated payment with ID: {} and status: {}", paymentId, responseDTO.getStatus());
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Void> deletePayment(@PathVariable UUID paymentId) {
        log.info("Received request to delete payment with ID: {}", paymentId);
        paymentService.deletePayment(paymentId);
        log.info("Successfully deleted payment with ID: {}", paymentId);
        return ResponseEntity.noContent().build();
    }
}
