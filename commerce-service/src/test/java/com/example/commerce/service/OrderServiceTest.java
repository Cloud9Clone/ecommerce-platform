package com.example.commerce.service;

import com.example.commerce.dto.OrderRequestDTO;
import com.example.commerce.dto.OrderResponseDTO;
import com.example.commerce.model.Order;
import com.example.commerce.model.User;
import com.example.commerce.model.enums.OrderStatus;
import com.example.commerce.model.enums.Role;
import com.example.commerce.repository.OrderRepository;
import com.example.commerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private UUID userId;
    private UUID orderId;

    @BeforeEach
    void setup() {
        testUser = new User();
        userId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        testUser.setUserId(userId);
        testUser.setName("Onyx");
        testUser.setEmail("onyx@corp.com");
        testUser.setPassword("password12345");
        testUser.setRole(Role.CUSTOMER);
    }

    @Test
    void testGetOrderForUser_Success() {
        Order order = createTestOrder(OrderStatus.PENDING);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(orderRepository.findByUserUserId(userId)).thenReturn(List.of(order));

        List<OrderResponseDTO> orders = orderService.getOrdersForUser(userId, Optional.empty());

        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals(OrderStatus.PENDING.toString(), orders.get(0).getStatus());
    }

    @Test
    void testGetOrdersForUser_UserNotFound() {
        when(userRepository.existsById(userId)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> orderService.getOrdersForUser(userId, Optional.empty()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void testCreateOrder_Success() {
        OrderRequestDTO requestDTO = new OrderRequestDTO(
          userId, "Hauptstraße 10", "Berlin", "Berlin", "Germany", "10115", new BigDecimal("300.00"), "PENDING"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
           Order order = invocation.getArgument(0);
           order.setOrderId(orderId);
           return order;
        });

        OrderResponseDTO responseDTO = orderService.createOrder(requestDTO);

        assertNotNull(responseDTO);
        assertEquals(orderId, responseDTO.getOrderId());
        assertEquals("Hauptstraße 10", responseDTO.getStreet());
    }

    @Test
    void testUpdateOrderStatus_Success() {
        Order order = createTestOrder(OrderStatus.PENDING);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponseDTO responseDTO = orderService.updateOrderStatus(orderId, OrderStatus.COMPLETED);

        assertNotNull(responseDTO);
        assertEquals(OrderStatus.COMPLETED.toString(), responseDTO.getStatus());
    }

    @Test
    void testDeleteOrder_Success() {
        Order order = createTestOrder(OrderStatus.PENDING);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.deleteOrder(orderId);

        verify(orderRepository, times(1)).delete(order);
    }

    private Order createTestOrder(OrderStatus status) {
        Order order = new Order();
        order.setOrderId(orderId);
        order.setUser(testUser);
        order.setState("Hauptstraße 10");
        order.setCity("Berlin");
        order.setState("Berlin");
        order.setCountry("Germany");
        order.setPostalCode("10115");
        order.setTotalPrice(new BigDecimal("100.00"));
        order.setStatus(status);
        return order;
    }
}
