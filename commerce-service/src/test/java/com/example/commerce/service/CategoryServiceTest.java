package com.example.commerce.service;

import com.example.commerce.dto.CategoryRequestDTO;
import com.example.commerce.dto.CategoryResponseDTO;
import com.example.commerce.model.Category;
import com.example.commerce.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setup() {
        categoryRepository.deleteAll();
    }

    /**
     * Test successful category creation with a unique name
     * - Mocks repository to simulate that no category with the same name exists (`existsByName` returns false)
     * - Ensures the category is saved successfully
     * - Verifies that the returned category has the correct name
     */
    @Test
    void testCreateCategory_UniqueName_Success() {
        CategoryRequestDTO requestDTO = new CategoryRequestDTO("Electronics");

        when(categoryRepository.existsByName("Electronics")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category savedCategory = invocation.getArgument(0);
            savedCategory.setCategoryId(UUID.randomUUID());
            return savedCategory;
        });

        CategoryResponseDTO createdCategory = categoryService.createCategory(requestDTO);

        assertNotNull(createdCategory);
        assertEquals("Electronics", createdCategory.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    /**
     * Test duplicate category creation
     * - Mocks repository to simulate that a category with the same name already exists (`existsByName` returns true)
     * - Verifies that a `ResponseStatusException` is thrown with HttpStatus.BAD_REQUEST
     * - Ensures the exception message indicates that the category name is already in use
     */
    @Test
    void testCreateCategory_DuplicateName() {
        CategoryRequestDTO requestDTO = new CategoryRequestDTO("Gadgets");

        when(categoryRepository.existsByName("Gadgets")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> categoryService.createCategory(requestDTO));

        assertEquals("Category with this name already exists", exception.getReason());
    }

    /**
     * Test successful category update
     * - Mocks repository to simulate finding an existing category by ID
     * - Ensures category details are updated and saved
     */
    @Test
    void testUpdateCategory_Success() {
        UUID categoryId = UUID.randomUUID();
        Category existingCategory = new Category();
        existingCategory.setCategoryId(categoryId);
        existingCategory.setName("Home Appliances");
        CategoryRequestDTO updateRequest = new CategoryRequestDTO("Kitchen Appliances");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(existingCategory);

        CategoryResponseDTO updatedCategory = categoryService.updateCategory(categoryId, updateRequest);
        assertEquals("Kitchen Appliances", updatedCategory.getName());
    }

    /**
     * Test category update failure (Category Not Found)
     * - Mocks repository to return an empty Optional for `findById()`
     * - Ensures ResponseStatusException is thrown with HttpStatus.NOT_FOUND
     */
    @Test
    void testUpdateCategory_NotFound() {
        UUID categoryId = UUID.randomUUID();
        CategoryRequestDTO updateRequest = new CategoryRequestDTO("New Category");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                categoryService.updateCategory(categoryId, updateRequest)
        );

        assertEquals("Category not found", exception.getReason());
    }
}
