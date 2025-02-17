package com.example.commerce.service;

import com.example.commerce.dto.ProductRequestDTO;
import com.example.commerce.dto.ProductResponseDTO;
import com.example.commerce.model.Category;
import com.example.commerce.model.Product;
import com.example.commerce.repository.CategoryRepository;
import com.example.commerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Category category;
    private Product product;

    @BeforeEach
    void setup() {
        category = new Category();
        category.setCategoryId(UUID.randomUUID());
        category.setName("Electronics");

        product = new Product();
        product.setProductId(UUID.randomUUID());
        product.setName("Headphones");
        product.setDescription("A high-quality pair of headphones");
        product.setCategory(category);
        product.setPrice(new BigDecimal("100.00"));
        product.setStock(10);
        product.setImageUrl("ExampleURL_Headphones");
    }

    /**
     * Test successful product creation
     * - Ensures that a product with a unique name is created successfully
     */
    @Test
    void testCreateProduct_Success() {
        ProductRequestDTO requestDTO = new ProductRequestDTO(
                "Headphones",
                "A high-quality pair of headphones",
                new BigDecimal("100.00"),
                10,
                "ExampleURL_Headphones",
                category.getCategoryId()
        );

        when(categoryRepository.findById(category.getCategoryId())).thenReturn(Optional.of(category));
        when(productRepository.existsByNameIgnoreCase("Headphones")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDTO createdProduct = productService.createProduct(requestDTO);

        assertNotNull(createdProduct);
        assertEquals("Headphones", createdProduct.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    /**
     * Test duplicate product creation
     * - Ensures that a `ResponseStatusException` is thrown for duplicate product names
     */
    @Test
    void testCreateProduct_DuplicateName() {
        ProductRequestDTO requestDTO = new ProductRequestDTO(
                "Headphones",
                "A high-quality pair of headphones",
                new BigDecimal("100.00"),
                10,
                "ExampleURL_Headphones",
                category.getCategoryId()
        );

        when(categoryRepository.findById(category.getCategoryId())).thenReturn(Optional.of(category));
        when(productRepository.existsByNameIgnoreCase("Headphones")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productService.createProduct(requestDTO);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Product with this name already exists", exception.getReason());
    }
}
