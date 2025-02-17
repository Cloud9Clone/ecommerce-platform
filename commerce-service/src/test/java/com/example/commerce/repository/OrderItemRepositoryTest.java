package com.example.commerce.repository;

import com.example.commerce.model.*;
import com.example.commerce.model.enums.OrderStatus;
import com.example.commerce.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class OrderItemRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private Order savedOrder;
    private Product savedProduct;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setName("Onyx");
        user.setEmail("onyx@corp.com");
        user.setPassword("password12345");
        user.setRole(Role.CUSTOMER);
        userRepository.save(user);

        savedOrder = new Order();
        savedOrder.setUser(user);
        savedOrder.setStatus(OrderStatus.PENDING);
        savedOrder.setStreet("Hauptstraße 10");
        savedOrder.setCity("Berlin");
        savedOrder.setState("Berlin");
        savedOrder.setCountry("Germany");
        savedOrder.setPostalCode("10115");
        savedOrder.setTotalPrice(new BigDecimal("500.00"));
        orderRepository.save(savedOrder);

        Category category = new Category();
        category.setName("Electronics");
        categoryRepository.save(category);

        savedProduct = new Product();
        savedProduct.setName("Laptop");
        savedProduct.setDescription("A very good laptop");
        savedProduct.setCategory(category);
        savedProduct.setPrice(new BigDecimal("50.00"));
        savedProduct.setStock(10);
        savedProduct.setImageUrl("ExampleURL_Laptop");
        productRepository.save(savedProduct);
    }

    @Test
    void testFindByOrder_Success() {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(savedOrder);
        orderItem.setProduct(savedProduct);
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("100.00"));
        orderItemRepository.save(orderItem);

        List<OrderItem> retrievedItems = orderItemRepository.findByOrder(savedOrder);
        assertEquals(1, retrievedItems.size());
        assertEquals(savedOrder.getOrderId(), retrievedItems.get(0).getOrder().getOrderId());
    }

    @Test
    void testFindByOrderAndProduct_Success() {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(savedOrder);
        orderItem.setProduct(savedProduct);
        orderItem.setQuantity(3);
        orderItem.setPrice(new BigDecimal("150.00"));
        orderItemRepository.save(orderItem);

        Optional<OrderItem> retrievedItem = orderItemRepository.findByOrderAndProduct(savedOrder, savedProduct);
        assertTrue(retrievedItem.isPresent());
        assertEquals(savedProduct.getProductId(), retrievedItem.get().getProduct().getProductId());
    }

    @Test
    void testFindByOrderAndProduct_NotFound() {
        Optional<OrderItem> retrievedItem = orderItemRepository.findByOrderAndProduct(savedOrder, savedProduct);
        assertFalse(retrievedItem.isPresent());
    }
}
