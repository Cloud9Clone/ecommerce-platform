package com.example.commerce.repository;

import com.example.commerce.model.Category;
import com.example.commerce.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setup() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    /**
     * Test product retrieval by category ID
     * - Ensures that the repository returns the correct products for a given category
     */
    @Test
    void testFindByCategoryId() {
        Category category = new Category();
        category.setName("Electronics");
        categoryRepository.save(category);

        Product product1 = new Product();
        product1.setName("Laptop");
        product1.setDescription("A very good laptop");
        product1.setCategory(category);
        product1.setPrice(new BigDecimal("1000.00"));
        product1.setStock(5);
        product1.setImageUrl("ExampleURL_Laptop");
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Smartphone");
        product2.setDescription("A very good smartphone");
        product2.setCategory(category);
        product2.setPrice(new BigDecimal("700.00"));
        product2.setStock(10);
        product2.setImageUrl("ExampleURL_Smartphone");
        productRepository.save(product2);

        List<Product> products = productRepository.findByCategory_CategoryId(category.getCategoryId());
        assertEquals(2, products.size());
    }

    /**
     * Test product existence check by name
     * - Ensures the `existsByName` method works correctly
     */
    @Test
    void testExistsByName() {
        Category category = new Category();
        category.setName("Electronics");
        categoryRepository.save(category);

        Product product = new Product();
        product.setName("Tablet");
        product.setDescription("A very good tablet");
        product.setCategory(category);
        product.setPrice(new BigDecimal("500.00"));
        product.setStock(8);
        product.setImageUrl("ExampleURL_Tablet");
        productRepository.save(product);

        boolean exists = productRepository.existsByNameIgnoreCase("Tablet");
        assertTrue(exists);

        boolean notExists = productRepository.existsByNameIgnoreCase("NonExistentProduct");
        assertFalse(notExists);
    }
}
