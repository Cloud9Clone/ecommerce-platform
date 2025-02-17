package com.example.commerce.dto;

import com.example.commerce.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private UUID userId;
    private String name;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
}



