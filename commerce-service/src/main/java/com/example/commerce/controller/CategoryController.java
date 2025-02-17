package com.example.commerce.controller;

import com.example.commerce.dto.CategoryRequestDTO;
import com.example.commerce.dto.CategoryResponseDTO;
import com.example.commerce.model.Category;
import com.example.commerce.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        log.info("Received request to retrieve all categories");
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable UUID categoryId) {
        log.info("Received request to fetch category with ID: {}", categoryId);
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO requestDTO) {
        log.info("Received request to create a new category");
        CategoryResponseDTO responseDTO = categoryService.createCategory(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable UUID categoryId, @Valid @RequestBody CategoryRequestDTO requestDTO) {
        log.info("Received request to update category with ID: {}", categoryId);
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, requestDTO));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Category> deleteCategory(@PathVariable UUID categoryId) {
        log.info("Received request to delete category with ID: {}", categoryId);
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
