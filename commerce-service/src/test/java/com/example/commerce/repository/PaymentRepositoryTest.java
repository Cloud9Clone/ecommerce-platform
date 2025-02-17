package com.example.commerce.repository;

import com.example.commerce.model.Order;
import com.example.commerce.model.Payment;
import com.example.commerce.model.User;
import com.example.commerce.model.enums.OrderStatus;
import com.example.commerce.model.enums.PaymentMethod;
import com.example.commerce.model.enums.PaymentStatus;
import com.example.commerce.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private Order order;
    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setName("Onyx");
        user.setEmail("onyx@corp.com");
        user.setPassword("password12345");
        user.setRole(Role.CUSTOMER);
        userRepository.save(user);

        order = new Order();
        order.setUser(user);
        order.setStreet("Hauptstrasse 10");
        order.setCity("Berlin");
        order.setState("Berlin");
        order.setCountry("Germany");
        order.setPostalCode("10115");
        order.setTotalPrice(new BigDecimal("200.00"));
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);
    }

    @Test
    void testExistsByOrderId_Success() {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(new BigDecimal("100.00"));
        payment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId(UUID.randomUUID().toString());
        paymentRepository.save(payment);

        boolean exists = paymentRepository.existsByOrderOrderId(order.getOrderId());
        assertTrue(exists);
    }

    @Test
    void testFindByStatus_Success() {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(new BigDecimal("200.00"));
        payment.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId(UUID.randomUUID().toString());
        paymentRepository.save(payment);

        List<Payment> completedPayment = paymentRepository.findByStatus(PaymentStatus.COMPLETED);
        assertEquals(1, completedPayment.size());
        assertEquals(PaymentStatus.COMPLETED, completedPayment.get(0).getStatus());
    }
}
