package com.example.commerce.controller;

import com.example.commerce.dto.ProductRequestDTO;
import com.example.commerce.model.Category;
import com.example.commerce.model.Product;
import com.example.commerce.repository.CategoryRepository;
import com.example.commerce.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    void setup() {
        category = new Category();
        category.setName("Electronics");
        category = categoryRepository.save(category);
    }

    /**
     * Test retrieving all products via API
     * - Ensures that the GET /api/products endpoint returns the correct number of products
     */
    @Test
    void testGetAllProducts_Success() throws Exception {
        Product product1 = new Product();
        product1.setName("Laptop");
        product1.setDescription("Powerful laptop");
        product1.setCategory(category);
        product1.setPrice(new BigDecimal("1200.00"));
        product1.setStock(5);
        product1.setImageUrl("laptop.jpg");

        Product product2 = new Product();
        product2.setName("Phone");
        product2.setDescription("Smartphone with great features");
        product2.setCategory(category);
        product2.setPrice(new BigDecimal("800.00"));
        product2.setStock(10);
        product2.setImageUrl("phone.jpg");

        productRepository.save(product1);
        productRepository.save(product2);

        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[1].name").value("Phone"));
    }

    /**
     * Test fetching products by category
     * - Ensures that products belonging to a specific category are correctly retrieved
     */
    @Test
    void testGetProductsByCategory_Success() throws Exception {
        Product product = new Product();
        product.setName("Smartwatch");
        product.setDescription("Wearable tech");
        product.setCategory(category);
        product.setPrice(new BigDecimal("250.00"));
        product.setStock(10);
        product.setImageUrl("smartwatch.jpg");
        productRepository.save(product);

        mockMvc.perform(get("/api/products/category/" + category.getCategoryId())
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.length()").value(1))
                        .andExpect(jsonPath("$[0].name").value("Smartwatch"));
    }

    @Test
    void testCreateProduct_Success() throws Exception {
        ProductRequestDTO requestDTO = new ProductRequestDTO(
                "Tablet",
                "Portable tablet with touchscreen",
                new BigDecimal("500.00"),
                8,
                "ExampleURL_Tablet",
                category.getCategoryId()
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.name").value("Tablet"))
                        .andExpect(jsonPath("$.price").value(500.00));
    }

    /**
     * Test product creation with missing required fields
     * - Ensures that the API returns a BAD_REQUEST when required fields are missing
     */
    @Test
    void testCreateProduct_MissingFields() throws Exception {
        Product product = new Product(); // Missing fields e.g., name, description, category, price, etc.

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    /**
     * Test product creation with a nonexistent category
     * - Ensures that the API returns NOT_FOUND if the category does not exist
     */
    @Test
    void testCreateProduct_CategoryNotFound() throws Exception {
        ProductRequestDTO requestDTO = new ProductRequestDTO(
                "Monitor",
                "High-resolution monitor",
                new BigDecimal("300.00"),
                15,
                "ExampleURL_Monitor",
                UUID.randomUUID()
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                        .andExpect(status().isNotFound());
    }

    /**
     * Test successful product update
     * - Ensures that an existing product can be updated with new values
     */
    @Test
    void testUpdateProduct_Success() throws Exception {
        Product product = new Product();
        product.setName("Washing Machine");
        product.setDescription("A very good washing machine");
        product.setCategory(category);
        product.setPrice(new BigDecimal("500.00"));
        product.setStock(10);
        product.setImageUrl("ExampleURL_WashingMachine");
        Product savedProduct = productRepository.save(product);

        ProductRequestDTO updateRequest = new ProductRequestDTO(
                "Washing Machine",
                "An updated washing machine",
                new BigDecimal("450.00"),
                15,
                "ExampleURL_WashingMachine_Updated",
                category.getCategoryId()
        );

        mockMvc.perform(put("/api/products/" + savedProduct.getProductId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.price").value(450.00))
                        .andExpect(jsonPath("$.stock").value(15))
                        .andExpect(jsonPath("$.description").value("An updated washing machine"))
                        .andExpect(jsonPath("$.imageUrl").value("ExampleURL_WashingMachine_Updated"));
    }

    /**
     * Test product deletion
     * - Ensures that a product can be successfully deleted and is no longer retrievable
     */
    @Test
    void testDeleteProduct_Success() throws Exception {
        Product product = new Product();
        product.setName("Mouse");
        product.setDescription("Wireless mouse");
        product.setCategory(category);
        product.setPrice(new BigDecimal("30.00"));
        product.setStock(25);
        product.setImageUrl("mouse.jpg");
        product = productRepository.save(product);

        mockMvc.perform(delete("/api/products/" + product.getProductId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/" + product.getProductId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
