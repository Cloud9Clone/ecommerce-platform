package com.example.commerce.service;

import com.example.commerce.dto.CategoryRequestDTO;
import com.example.commerce.dto.CategoryResponseDTO;
import com.example.commerce.model.Category;
import com.example.commerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponseDTO> getAllCategories() {
        log.info("Retrieving all categories");

        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public CategoryResponseDTO getCategoryById(UUID categoryId) {
        log.info("Retrieving category with ID: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("Category with ID: {} not found", categoryId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
                });

        return mapToResponseDTO(category);
    }

    public CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO) {
        log.info("Creating new category with name: {}", requestDTO.getName());

        if (categoryRepository.existsByName(requestDTO.getName())) {
            log.error("Category new category with name: {}", requestDTO.getName());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category with this name already exists");
        }

        Category category = new Category();
        category.setName(requestDTO.getName());

        Category savedCategory = categoryRepository.save(category);
        log.info("Category '{}' created successfully with ID: {}", savedCategory.getName(), savedCategory.getCategoryId());

        return mapToResponseDTO(savedCategory);
    }

    public CategoryResponseDTO updateCategory(UUID categoryId, CategoryRequestDTO requestDTO) {
        log.info("Updating category with ID: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("Category with ID: {} not found", categoryId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
                });

        category.setName(requestDTO.getName());
        Category updatedCategory = categoryRepository.save(category);
        log.info("Category with ID: {} updated successfully", updatedCategory.getCategoryId());

        return mapToResponseDTO(updatedCategory);
    }

    public void deleteCategory(UUID categoryId) {
        log.info("Deleting category with ID: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("Category with ID: {} not found", categoryId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
                });

        categoryRepository.delete(category);
        log.info("Category with ID {} deleted successfully", categoryId);
    }

    private CategoryResponseDTO mapToResponseDTO(Category category) {
        return new CategoryResponseDTO(category.getCategoryId(), category.getName());
    }
}
