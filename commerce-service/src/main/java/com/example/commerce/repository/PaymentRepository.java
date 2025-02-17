package com.example.commerce.repository;

import com.example.commerce.model.Payment;
import com.example.commerce.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByOrderOrderId(UUID orderId);
    List<Payment> findByStatus(PaymentStatus status);
    boolean existsByOrderOrderId(UUID orderId);
    boolean existsByTransactionId(String transactionId);
}
