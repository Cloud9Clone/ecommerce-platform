package com.example.commerce.repository;

import com.example.commerce.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setup() {
        categoryRepository.deleteAll();
    }

    /**
     * Test finding a category by ID
     * - Saves a new category and retrieves it by ID
     * - Ensures the retrieved category matches the saved one
     */
    @Test
    void testFindById() {
        Category category = new Category();
        category.setName("Books");
        category = categoryRepository.save(category);

        Optional<Category> foundCategory = categoryRepository.findById(category.getCategoryId());
        assertTrue(foundCategory.isPresent());
        assertEquals("Books", foundCategory.get().getName());
    }

    /**
     * Test retrieving all categories
     * - Saves multiple categories and retrieves them all
     * - Ensures the number of retrieved categories matches the saved ones
     */
    @Test
    void testFindAll() {
        Category category1 = new Category();
        category1.setName("Toys");
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setName("Clothing");
        categoryRepository.save(category2);

        List<Category> categories = categoryRepository.findAll();
        assertEquals(2, categories.size());
    }

    /**
     * Test existence check by category name
     * - Ensures the `existsByName` method returns true for an existing category
     */
    @Test
    void testExistsByName() {
        Category category = new Category();
        category.setName("Electronics");
        categoryRepository.save(category);

        boolean exists = categoryRepository.existsByName("Electronics");
        assertTrue(exists);

        boolean notExists = categoryRepository.existsByName("NonExistent");
        assertFalse(notExists);
    }

    /**
     * Test saving a new category
     * - Ensures category is successfully saved in the repository
     * - Verifies that the returned category has a generated ID
     */
    @Test
    void testSaveCategory() {
        Category category = new Category();
        category.setName("Electronics");
        Category savedCategory = categoryRepository.save(category);

        assertNotNull(savedCategory.getCategoryId());
        assertEquals("Electronics", savedCategory.getName());
    }

    /**
     * Test deleting a category
     * - Saves a new category, deletes it, and verifies it no longer exists
     */
    @Test
    void testDeleteCategory() {
        Category category = new Category();
        category.setName("Fashion");
        category = categoryRepository.save(category);

        categoryRepository.deleteById(category.getCategoryId());

        Optional<Category> deletedCategory = categoryRepository.findById(category.getCategoryId());
        assertFalse(deletedCategory.isPresent());
    }
}
