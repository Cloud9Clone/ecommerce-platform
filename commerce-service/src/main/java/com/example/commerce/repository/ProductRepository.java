package com.example.commerce.repository;

import com.example.commerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByCategory_CategoryId(UUID categoryId); // Fetch products by category
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE LOWER(p.name) = LOWER(TRIM(:name))")
    boolean existsByNameIgnoreCase(@Param("name") String name); // Check for duplicate product names
}
