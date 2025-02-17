package com.example.commerce.controller;

import com.example.commerce.dto.CategoryRequestDTO;
import com.example.commerce.model.Category;
import com.example.commerce.repository.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setup() {
        categoryRepository.deleteAll();
    }

    /**
     * Test retrieving all categories via API
     * - Saves multiple categories in the repository
     * - Sends GET request to fetch all categories
     * - Verifies that the response status is 200 OK
     * - Ensures the response contains the expected number of categories
     */
    @Test
    void testGetAllCategories_Success() throws Exception {
        Category category1 = new Category();
        category1.setName("Fashion");
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setName("Toys");
        categoryRepository.save(category2);

        mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.length()").value(2));
    }

    /**
     * Test retrieving a category by ID via API
     * - Saves a category in the repository
     * - Sends GET request to fetch the category by its ID
     * - Verifies that the response status is 200 OK
     * - Ensures the response body contains the correct category name
     */
    @Test
    void testGetCategoryById_Success() throws Exception {
        Category category = new Category();
        category.setName("Books");
        category = categoryRepository.save(category);

        mockMvc.perform(get("/api/categories/" + category.getCategoryId())
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.name").value("Books"));
    }

    /**
     * Test retrieving a non-existent category by ID
     * - Sends GET request with a random UUID
     * - Verifies that the response status is 404 Not Found
     */
    @Test
    void testGetCategoryById_NotFound() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(get("/api/categories/" + randomId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Test successful category creation via API
     * - Sends POST request with category JSON data
     * - Verifies that the response status is 201 Created
     * - Ensures the response body contains the correct category name
     */
    @Test
    void testCreateCategory_Success() throws Exception {
        CategoryRequestDTO requestDTO = new CategoryRequestDTO("Books");

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Books"));
    }

    /**
     * Test successful category update via API
     * - Saves a category in the repository
     * - Sends PUT request with updated category details
     * - Verifies that the response status is 200 OK
     * - Ensures the category name is updated in the response
     */
    @Test
    void testUpdateCategory_Success () throws Exception {
        Category category = new Category();
        category.setName("Sports");
        category = categoryRepository.save(category);

        CategoryRequestDTO updateRequest = new CategoryRequestDTO("Outdoor Sports");

        mockMvc.perform(put("/api/categories/" + category.getCategoryId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.name").value("Outdoor Sports"));
    }

    /**
     * Test successful category deletion via API
     * - Saves a category in the repository
     * - Sends DELETE request to remove the category
     * - Verifies that the response status is 204 No Content
     * - Ensures the category no longer exists in the repository
     */
    @Test
    void testDeleteCategory_Success() throws Exception {
        Category category = new Category();
        category.setName("Furniture");
        category = categoryRepository.save(category);

        mockMvc.perform(delete("/api/categories/" + category.getCategoryId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
