package com.example.commerce.service;

import com.example.commerce.dto.OrderItemRequestDTO;
import com.example.commerce.dto.OrderItemResponseDTO;
import com.example.commerce.model.*;
import com.example.commerce.model.enums.OrderStatus;
import com.example.commerce.model.enums.Role;
import com.example.commerce.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderItemService orderItemService;

    private Order savedOrder;
    private Product savedProduct;
    private OrderItem savedOrderItem;

    private UUID orderId;
    private UUID productId;

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
        orderId = savedOrder.getOrderId();

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
        productId = savedProduct.getProductId();

        savedOrderItem = new OrderItem();
        savedOrderItem.setOrder(savedOrder);
        savedOrderItem.setProduct(savedProduct);
        savedOrderItem.setQuantity(2);
        savedOrderItem.setPrice(new BigDecimal("100.00"));
    }

    @Test
    void testGetOrderItemsByOrderId_Success() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(savedOrder));
        when(orderItemRepository.findByOrder(savedOrder)).thenReturn(List.of(savedOrderItem));

        List<OrderItemResponseDTO> responseDTOS = orderItemService.getOrderItemsByOrderId(orderId);

        assertEquals(1, responseDTOS.size());
        assertEquals(orderId, responseDTOS.get(0).getOrderItemId());
        verify(orderItemRepository, times(1)).findByOrder(savedOrder);
    }

    @Test
    void testAddOrUpdateOrderItem_InsufficientStock() {
        OrderItemRequestDTO requestDTO = new OrderItemRequestDTO(orderId, productId, 20, new BigDecimal("500.00"));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(savedOrder));
        when(productRepository.findById(productId)).thenReturn(Optional.of(savedProduct));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> orderItemService.addOrUpdateOrderItem(requestDTO));

        assertTrue(exception.getMessage().contains("Insufficient stock for the product"));
    }
}
