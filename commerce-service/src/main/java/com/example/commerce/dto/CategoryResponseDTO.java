package com.example.commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CategoryResponseDTO {
    private UUID categoryId;
    private String name;
}
