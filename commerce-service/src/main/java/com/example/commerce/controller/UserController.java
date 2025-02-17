package com.example.commerce.controller;

import com.example.commerce.dto.UserRequestDTO;
import com.example.commerce.dto.UserResponseDTO;
import com.example.commerce.model.User;
import com.example.commerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get user by ID", description = "Retrieve user details by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID userId) {
        log.info("Received request to retrieve user with ID: {}", userId);
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @Operation(summary = "Get all users", description = "Retrieve a list of all registered users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = User.class)))
    })
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.info("Received request to fetch all users");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Create a new user", description = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        log.info("Received request to create a new user");
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(requestDTO));
    }

    @Operation(summary = "Update a user", description = "Update a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable UUID userId, @Valid @RequestBody UserRequestDTO requestDTO) {
        log.info("Received request to update user with ID: {}", userId);
        return ResponseEntity.ok(userService.updateUser(userId, requestDTO));
    }

    @Operation(summary = "Delete a user", description = "Delete a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        log.info("Received request to delete user with ID: {}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
