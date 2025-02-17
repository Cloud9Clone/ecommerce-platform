package com.example.commerce.repository;

import com.example.commerce.model.Order;
import com.example.commerce.model.User;
import com.example.commerce.model.enums.OrderStatus;
import com.example.commerce.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@ActiveProfiles("test")
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setName("Onyx");
        testUser.setEmail("onyx@corp.com");
        testUser.setPassword("password12345");
        testUser.setRole(Role.CUSTOMER);
        userRepository.save(testUser);
    }

    @Test
    void testFindByUserUserId_Success() {
        Order order = createTestOrder(testUser, OrderStatus.PENDING, new BigDecimal("100.00"));
        orderRepository.save(order);

        List<Order> orders = orderRepository.findByUserUserId(testUser.getUserId());

        assertFalse(orders.isEmpty());
        assertEquals(1, orders.size());
        assertEquals(OrderStatus.PENDING, orders.get(0).getStatus());
    }

    @Test
    void testFindByUserUserIdAndStatus_Success() {
        Order order1 = createTestOrder(testUser, OrderStatus.PENDING, new BigDecimal("100.00"));
        Order order2 = createTestOrder(testUser, OrderStatus.COMPLETED, new BigDecimal("200.00"));
        orderRepository.save(order1);
        orderRepository.save(order2);

        List<Order> pendingOrders = orderRepository.findByUserUserIdAndStatus(testUser.getUserId(), OrderStatus.PENDING);

        assertEquals(1, pendingOrders.size());
        assertEquals(OrderStatus.PENDING, pendingOrders.get(0).getStatus());
    }

    private Order createTestOrder(User user, OrderStatus status, BigDecimal totalPrice) {
        Order order = new Order();
        order.setUser(user);
        order.setStreet("Hauptstraße 10");
        order.setCity("Berlin");
        order.setState("Berlin");
        order.setCountry("Germany");
        order.setPostalCode("10115");
        order.setTotalPrice(totalPrice);
        order.setStatus(status);
        return order;
    }
}
