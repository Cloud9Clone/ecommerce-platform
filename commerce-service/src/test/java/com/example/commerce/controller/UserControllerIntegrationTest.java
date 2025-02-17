package com.example.commerce.controller;

import com.example.commerce.dto.UserRequestDTO;
import com.example.commerce.model.User;
import com.example.commerce.model.enums.Role;
import com.example.commerce.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        testUser = new User();
        testUser.setName("Onyx");
        testUser.setEmail("onyx@corp.com");
        testUser.setPassword("password123");
        testUser.setRole(Role.CUSTOMER);
        userRepository.save(testUser);
    }

    /**
     * Test fetching all users when database is empty
     * - Expects empty JSON array in response
     */
    @Test
    void testGetAllUsers_Empty() throws Exception {
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.length()").value(1))
                        .andExpect(jsonPath("$[0].email").value(testUser.getEmail()));
    }

    /**
     * Test successful user creation via API
     * - Sends POST request with valid user data
     * - Expects HTTP 201 Created and correct JSON response
     */
    @Test
    void testCreateUser_Success() throws Exception {
        UserRequestDTO requestDTO = new UserRequestDTO(
                "Alice", "alice@corp.com", "password123", Role.CUSTOMER
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.email").value("alice@corp.com"));
    }

    /**
     * Test user creation failure due to duplicate email
     * - Inserts a user first
     * - Sends duplicate POST request
     * - Expects HTTP 400 Bad Request
     */
    @Test
    void testCreateUser_DuplicateEmail() throws Exception {
        UserRequestDTO requestDTO = new UserRequestDTO(
                "Onyx", "onyx@corp.com", "password123", Role.CUSTOMER
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                        .andExpect(status().isConflict())
                        .andExpect(content().string("Email is already in use"));
    }

    /**
     * Test successful user deletion via API
     * - Inserts user first
     * - Sends DELETE request
     * - Expects HTTP 204 No Content
     */
    @Test
    void testDeleteUser_Success() throws Exception {
        mockMvc.perform(delete("/api/users/{userId}", testUser.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNoContent());
    }

    /**
     * Test user deletion failure (User Not Found)
     * - Sends DELETE request for non-existent user ID
     * - Expects HTTP 404 Not Found
     */
    @Test
    void testDeleteUser_NotFound() throws Exception {
        mockMvc.perform(delete("/api/users/{userId}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(content().string("User not found"));
    }
}
