package com.example.commerce.service;


import com.example.commerce.dto.PaymentRequestDTO;
import com.example.commerce.dto.PaymentResponseDTO;
import com.example.commerce.model.Order;
import com.example.commerce.model.Payment;
import com.example.commerce.model.User;
import com.example.commerce.model.enums.OrderStatus;
import com.example.commerce.model.enums.PaymentMethod;
import com.example.commerce.model.enums.PaymentStatus;
import com.example.commerce.model.enums.Role;
import com.example.commerce.repository.OrderRepository;
import com.example.commerce.repository.PaymentRepository;
import com.example.commerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    private User user;
    private Order savedOrder;

    @BeforeEach
    void setup() {
        user = new User();
        user.setName("Onyx");
        user.setEmail("onyx@corp.com");
        user.setPassword("password12345");
        user.setRole(Role.CUSTOMER);
        userRepository.save(user);

        savedOrder = new Order();
        savedOrder.setUser(user);
        savedOrder.setStreet("Hauptstrasse 10");
        savedOrder.setCity("Berlin");
        savedOrder.setState("Berlin");
        savedOrder.setCountry("Germany");
        savedOrder.setPostalCode("10115");
        savedOrder.setTotalPrice(new BigDecimal("200.00"));
        savedOrder.setStatus(OrderStatus.PENDING);
        orderRepository.save(savedOrder);
    }

    @Test
    void testCreatePayment_Success() {
        PaymentRequestDTO requestDTO = new PaymentRequestDTO(
                savedOrder.getOrderId(), new BigDecimal("200.00"), PaymentMethod.CREDIT_CARD, UUID.randomUUID().toString()
        );

        when(orderRepository.findById(savedOrder.getOrderId())).thenReturn(Optional.of(savedOrder));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
           Payment payment = invocation.getArgument(0);
           payment.setPaymentId(UUID.randomUUID());
           return payment;
        });

        PaymentResponseDTO responseDTO = paymentService.createPayment(requestDTO);
        System.out.println(responseDTO.toString());

        assertNotNull(responseDTO);
        assertEquals(PaymentMethod.CREDIT_CARD, responseDTO.getPaymentMethod());
        assertEquals(savedOrder.getOrderId(), responseDTO.getOrderId());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testCreatePayment_OrderNotFound() {
        PaymentRequestDTO requestDTO = new PaymentRequestDTO(
          savedOrder.getOrderId(), new BigDecimal("100.00"), PaymentMethod.BANK_TRANSFER, "unique-transaction-id"
        );

        when(orderRepository.findById(savedOrder.getOrderId())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> paymentService.createPayment(requestDTO));

        assertTrue(exception.getMessage().contains("Order not found"));
    }

    @Test
    void testUpdatePaymentStatus_Failure_CompletedPayment() {
        Payment existingPayment = new Payment();
        existingPayment.setPaymentId(UUID.randomUUID());
        existingPayment.setOrder(savedOrder);
        existingPayment.setAmount(new BigDecimal("100.00"));
        existingPayment.setStatus(PaymentStatus.COMPLETED);
        existingPayment.setPaymentMethod(PaymentMethod.BANK_TRANSFER);

        when(paymentRepository.findById(existingPayment.getPaymentId())).thenReturn(Optional.of(existingPayment));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> paymentService.updatePaymentStatus(existingPayment.getPaymentId(), PaymentStatus.FAILED));

        assertTrue(exception.getMessage().contains("Cannot update a completed payment"));
        verify(paymentRepository, never()).save(any(Payment.class));
    }
}
