package com.example.commerce.service;

import com.example.commerce.dto.PaymentRequestDTO;
import com.example.commerce.dto.PaymentResponseDTO;
import com.example.commerce.model.Order;
import com.example.commerce.model.Payment;
import com.example.commerce.model.enums.OrderStatus;
import com.example.commerce.model.enums.PaymentStatus;
import com.example.commerce.repository.OrderRepository;
import com.example.commerce.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentResponseDTO getPaymentById(UUID paymentId) {
        log.info("Retrieving payment details for Payment ID: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));

        return mapToResponseDTO(payment);
    }

    @Transactional
    public PaymentResponseDTO createPayment(PaymentRequestDTO requestDTO) {
        log.info("Creating payment for Order ID: {}", requestDTO.getOrderId());

        Order order = orderRepository.findById(requestDTO.getOrderId())
                .orElseThrow(() -> {
                   log.error("Order not found for Order ID: {}", requestDTO.getOrderId());
                   return new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
                });

        // Check if payment already exists for the order
        if (paymentRepository.existsByOrderOrderId(requestDTO.getOrderId())) {
            log.error("Payment already exists for Order ID: {}", requestDTO.getOrderId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment already exists for this order");
        }

        // Check if transaction ID is not already used in other payment
        if (paymentRepository.existsByTransactionId(requestDTO.getTransactionId())) {
            log.error("Transaction with ID {} already exists", requestDTO.getTransactionId());
            throw new IllegalArgumentException("Transaction ID already exists");
        }

        // Cannot process payment for a non-pending order
        if (order.getStatus() != OrderStatus.PENDING) {
            log.error("Order with ID: {} has a non-pending status", order.getOrderId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot process payment for a non-existing order");
        }

        // Validate the payment amount
        if (requestDTO.getAmount().compareTo(order.getTotalPrice()) < 0) {
            log.error("Payment amount {} does not match order amount {}", requestDTO.getAmount(), order.getTotalPrice());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment amount is less than the order total");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(requestDTO.getAmount());
        payment.setPaymentMethod(requestDTO.getPaymentMethod());
        payment.setStatus(PaymentStatus.COMPLETED);             // For the beginning
        payment.setTransactionId(UUID.randomUUID().toString()); // For the beginning

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created successfully for Order ID: {}", requestDTO.getOrderId());

        return mapToResponseDTO(savedPayment);
    }

    public PaymentResponseDTO updatePaymentStatus(UUID paymentId, PaymentStatus newStatus) {
        log.info("Updating payment status for Payment ID: {} to {}", paymentId, newStatus);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                   log.error("Payment not found for Payment ID: {}", paymentId);
                   return new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found");
                });

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            log.warn("Attempted to update a completed payment. Payment ID: {}", paymentId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update a completed payment");
        }

        payment.setStatus(newStatus);
        Payment updatedPayment = paymentRepository.save(payment);
        log.info("Payment status updated successfully for Payment ID: {}", paymentId);

        return mapToResponseDTO(updatedPayment);
    }

    public void deletePayment(UUID paymentId) {
        log.info("Attempting to delete payment with ID: {}", paymentId);

        // Check if payment exists
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));

        // Cannot delete a completed payment
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            log.warn("Cannot delete a completed payment with ID: {}", paymentId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Completed payments cannot be deleted.");
        }

        paymentRepository.delete(payment);
        log.info("Payment with ID: {} deleted successfully", paymentId);
    }

    private PaymentResponseDTO mapToResponseDTO(Payment payment) {
        return new PaymentResponseDTO(
            payment.getPaymentId(),
            payment.getOrder().getOrderId(),
            payment.getAmount(),
            payment.getPaymentMethod(),
            payment.getStatus().toString(),
            payment.getTransactionId(),
            payment.getCreatedAt()
        );
    }
}
