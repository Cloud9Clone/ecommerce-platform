package com.example.commerce.controller;


import com.example.commerce.dto.PaymentRequestDTO;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PaymentControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private User testUser;
    private Order testOrder;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        testUser = new User();
        testUser.setName("Onyx");
        testUser.setEmail("onyx@corp.com");
        testUser.setPassword("password12345");
        testUser.setRole(Role.CUSTOMER);
        userRepository.save(testUser);

        testOrder = new Order();
        testOrder.setUser(testUser);
        testOrder.setStreet("Hauptstrasse 10");
        testOrder.setCity("Berlin");
        testOrder.setState("Berlin");
        testOrder.setCountry("Germany");
        testOrder.setPostalCode("10115");
        testOrder.setTotalPrice(new BigDecimal("200.00"));
        testOrder.setStatus(OrderStatus.PENDING);
        orderRepository.save(testOrder);

        log.info("Test setup complete. Test user and order created.");
    }

    @Test
    void testCreatePayment_Success() throws Exception {
        log.info("Testing POST /api/orders/{}/payments", testOrder.getOrderId());

        PaymentRequestDTO requestDTO = new PaymentRequestDTO(
                testOrder.getOrderId(), new BigDecimal("200.00"), PaymentMethod.CREDIT_CARD, UUID.randomUUID().toString()
        );

        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(testOrder.getOrderId().toString()))
                .andExpect(jsonPath("$.paymentMethod").value("CREDIT_CARD"))
                .andExpect(jsonPath("$.amount").value(200.00));

        log.info("Successfully tested POST /api/orders/{}/payments", testOrder.getOrderId());
    }

    @Test
    void testGetPaymentId_Success() throws Exception {
        log.info("Testing GET /api/payments/{} for existing payment", testOrder.getOrderId());

        Payment payment = createTestPayment(testOrder, PaymentMethod.CREDIT_CARD);
        paymentRepository.save(payment);

        mockMvc.perform(get("/api/payments/{paymentId}", payment.getPaymentId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(payment.getPaymentId().toString()))
                .andExpect(jsonPath("$.paymentMethod").value("CREDIT_CARD"));

        log.info("Successfully tested GET /api/payments/{}", payment.getPaymentId());
    }

    @Test
    void testUpdatePaymentStatus_Success() throws Exception {
        log.info("Testing PUT /api/payments/{}/status for updating status", testOrder.getOrderId());

        Payment payment = createTestPayment(testOrder, PaymentMethod.BANK_TRANSFER);
        paymentRepository.save(payment);

        mockMvc.perform(put("/api/payments/{paymentId}/status?newStatus=COMPLETED", payment.getPaymentId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"COMPLETED\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        log.info("Successfully tested PUT /api/payments/{}/status", payment.getPaymentId());
    }

    @Test
    void testDeletePayment_Success() throws Exception {
        log.info("Testing DELETE /api/payments/{} for deleting payment", testOrder.getOrderId());

        Payment payment = createTestPayment(testOrder, PaymentMethod.PAYPAL);
        paymentRepository.save(payment);

        mockMvc.perform(delete("/api/payments/{paymentId}", payment.getPaymentId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        log.info("Successfully tested DELETE /api/payments/{}", payment.getPaymentId());
    }

    @Test
    void testCreatePayment_OrderNotFound() throws Exception {
        log.info("Testing POST /api/orders/{}/payments with non-existing order", UUID.randomUUID());

        PaymentRequestDTO requestDTO = new PaymentRequestDTO(
                UUID.randomUUID(), new BigDecimal("200.00"), PaymentMethod.CREDIT_CARD, UUID.randomUUID().toString()
        );

        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Order not found"));

        log.info("Successfully tested POST /api/orders/{}/payments with non-existent order", UUID.randomUUID());
    }

    private Payment createTestPayment(Order order, PaymentMethod paymentMethod) {
        Payment payment = new Payment();
        payment.setOrder(testOrder);
        payment.setAmount(order.getTotalPrice());
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId(UUID.randomUUID().toString());
        return payment;
    }
}
