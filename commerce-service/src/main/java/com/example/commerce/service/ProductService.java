package com.example.commerce.service;

import com.example.commerce.dto.ProductRequestDTO;
import com.example.commerce.dto.ProductResponseDTO;
import com.example.commerce.model.Category;
import com.example.commerce.model.Product;
import com.example.commerce.repository.CategoryRepository;
import com.example.commerce.repository.ProductRepository;
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
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductResponseDTO getProductById(UUID productId) {
        log.info("Retrieving product with ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product with ID: {} not found", productId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
                });

        return mapToResponseDTO(product);
    }

    public List<ProductResponseDTO> getProductsByCategory(UUID categoryId) {
        log.info("Retrieving products for category ID: {}", categoryId);

        if (!categoryRepository.existsById(categoryId)) {
            log.error("Category with ID {} not found", categoryId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }

        return productRepository.findByCategory_CategoryId(categoryId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDTO> getAllProducts() {
        log.info("Retrieving all products");
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) {
        log.info("Creating new product: {}", requestDTO.getName());

        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> {
                    log.error("Category with ID {} not found", requestDTO.getCategoryId());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
                });

        String normalizedProductName = requestDTO.getName().trim();

        if (productRepository.existsByNameIgnoreCase(normalizedProductName)) {
            log.error("Product with name: {} already exists", normalizedProductName);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product with this name already exists");
        }

        Product product = new Product();
        product.setName(requestDTO.getName());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setStock(requestDTO.getStock());
        product.setImageUrl(requestDTO.getImageUrl());
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with ID: {}", savedProduct.getProductId());

        return mapToResponseDTO(savedProduct);
    }

    public ProductResponseDTO updateProduct(UUID productId, ProductRequestDTO requestDTO) {
        log.info("Updating product with ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product with ID {} not found", productId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
                });

        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> {
                    log.error("Category with ID {} not found", requestDTO.getCategoryId());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
                });

        product.setName(requestDTO.getName());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setStock(requestDTO.getStock());
        product.setImageUrl(requestDTO.getImageUrl());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with ID: {}", updatedProduct.getProductId());

        return mapToResponseDTO(updatedProduct);
    }

    public void deleteProduct(UUID productId) {
        log.info("Deleting product with ID: {}", productId);

        if (!productRepository.existsById(productId)) {
            log.error("Product with ID {} not found", productId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        productRepository.deleteById(productId);
        log.info("Product with ID {} deleted successfully", productId);
    }

    private ProductResponseDTO mapToResponseDTO(Product product) {
        return new ProductResponseDTO(
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImageUrl(),
                product.getCategory().getCategoryId()
        );
    }
}
