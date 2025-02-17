package com.example.commerce.controller;

import com.example.commerce.dto.ProductRequestDTO;
import com.example.commerce.dto.ProductResponseDTO;
import com.example.commerce.model.Product;
import com.example.commerce.service.ProductService;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable UUID productId) {
        log.info("Received request to get product with ID: {}", productId);
        ProductResponseDTO responseDTO = productService.getProductById(productId);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        log.info("Received request to get all products");
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByCategory(@PathVariable UUID categoryId) {
        log.info("Received request to get products for category ID: {}", categoryId);
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO requestDTO) {
        log.info("Received request to create a new product: {}", requestDTO.getName());
        ProductResponseDTO responseDTO = productService.createProduct(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable UUID productId, @Valid @RequestBody ProductRequestDTO requestDTO) {
        log.info("Received request to update product with ID: {}", productId);
        ProductResponseDTO responseDTO = productService.updateProduct(productId, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID productId) {
        log.info("Received request to delete product with ID: {}", productId);
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
