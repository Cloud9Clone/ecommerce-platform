package com.example.commerce.controller;

import com.example.commerce.dto.OrderRequestDTO;
import com.example.commerce.model.Order;
import com.example.commerce.model.User;
import com.example.commerce.model.enums.OrderStatus;
import com.example.commerce.model.enums.Role;
import com.example.commerce.repository.OrderRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OrderControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

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
        testOrder.setStreet("Hauptstraße 10");
        testOrder.setCity("Berlin");
        testOrder.setState("Berlin");
        testOrder.setCountry("Germany");
        testOrder.setPostalCode("10115");
        testOrder.setTotalPrice(new BigDecimal("250.00"));
        testOrder.setStatus(OrderStatus.PENDING);
        orderRepository.save(testOrder);

        log.info("Setup complete with test user: {} and order ID: {}", testUser.getUserId(), testOrder.getOrderId());
    }

    @Test
    void testGetOrderById_Success() throws Exception {
        mockMvc.perform(get("/api/orders/{orderId}", testOrder.getOrderId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(testOrder.getOrderId().toString()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testGetOrderById_NotFound() throws Exception {
        UUID nonExistentOrderId = UUID.randomUUID();

        mockMvc.perform(get("/api/orders/{orderId}", nonExistentOrderId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetOrdersForUser_Success() throws Exception {
        mockMvc.perform(get("/api/orders/user/{userId}", testUser.getUserId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].totalPrice").value(250.00));
    }

    @Test
    void testCreateOrder_Success() throws Exception {
        OrderRequestDTO requestDTO = new OrderRequestDTO(
                testUser.getUserId(),
                "Hauptstraße 10", "Berlin", "Berlin", "Germany", "10115",
                new BigDecimal("300.00"), "PENDING"
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.street").value("Hauptstraße 10"));
    }

    @Test
    void testCreateOrder_ValidationError() throws Exception {
        OrderRequestDTO requestDTO = new OrderRequestDTO(
                testUser.getUserId(),
                "", "Berlin", "Berlin", "Germany", "10115",
                new BigDecimal("300.00"), "PENDING"
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateOrderStatus_Success() throws Exception {
        mockMvc.perform(put("/api/orders/{orderId}/status", testOrder.getOrderId())
                .param("newStatus", "COMPLETED")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void testDeleteOrder_Success() throws Exception {
        mockMvc.perform(delete("/api/orders/{orderId}", testOrder.getOrderId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/orders/{orderId}", testOrder.getOrderId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
