package com.example.commerce.controller;

import com.example.commerce.dto.ShippingAddressRequestDTO;
import com.example.commerce.model.ShippingAddress;
import com.example.commerce.model.User;
import com.example.commerce.model.enums.Role;
import com.example.commerce.repository.ShippingAddressRepository;
import com.example.commerce.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ShippingAddressControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ShippingAddressRepository shippingAddressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private User testUser;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        testUser = new User();
        testUser.setName("Onyx");
        testUser.setEmail("onyx@corp.com");
        testUser.setPassword("password12345");
        testUser.setRole(Role.CUSTOMER);
        userRepository.save(testUser);

        log.info("Test setup completed. Test user created with ID: {}", testUser.getUserId());
    }

    @Test
    void testGetShippingAddressesForUser_Success() throws Exception {
        log.info("Testing GET /api/users/{}/addresses", testUser.getUserId());

        ShippingAddress address = createTestShippingAddress(testUser, "123 Test Street");
        shippingAddressRepository.save(address);

        mockMvc.perform(get("/api/users/{userId}/addresses", testUser.getUserId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].street").value("123 Test Street"));

        log.info("Successfully tested GET /api/users/{}/addresses", testUser.getUserId());
    }

    @Test
    void testGetShippingAddressById_NotFound() throws Exception {
        log.info("Testing GET /api/users/{}/addresses/{addressId} for not found", testUser.getUserId());

        UUID nonExistentAddressId = UUID.randomUUID();

        mockMvc.perform(get("/api/users/{userId}/addresses/{addressId}", testUser.getUserId(), nonExistentAddressId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Shipping address not found"));

        log.error("Address with ID {} not found during GET request", nonExistentAddressId);
    }

    @Test
    void testCreateShippingAddress_Success() throws Exception {
        log.info("Testing POST /api/users/{}/addresses", testUser.getUserId());

        ShippingAddressRequestDTO requestDTO = new ShippingAddressRequestDTO(
                "456 New Street", "New City", "New State", "New Country", "54321"
        );

        mockMvc.perform(post("/api/users/{userId}/addresses", testUser.getUserId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.street").value("456 New Street"))
                .andExpect(jsonPath("$.postalCode").value("54321"));

        log.info("Successfully tested POST /api/users/{}/addresses", testUser.getUserId());
    }

    @Test
    void testCreateShippingAddress_DuplicateConflict() throws Exception {
        log.info("Testing POST /api/users/{}/addresses for conflict", testUser.getUserId());

        ShippingAddressRequestDTO requestDTO = new ShippingAddressRequestDTO(
                "789 Duplicate Street", "Duplicate City", "Duplicate State", "Duplicate Country", "67890"
        );

        ShippingAddress shippingAddress = createTestShippingAddress(testUser, "789 Duplicate Street");
        shippingAddress.setCity("Duplicate City");
        shippingAddress.setState("Duplicate State");
        shippingAddress.setCountry("Duplicate Country");
        shippingAddress.setPostalCode("67890");
        shippingAddressRepository.save(shippingAddress);

        mockMvc.perform(post("/api/users/{userId}/addresses", testUser.getUserId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Shipping address already exists for this user"));

        log.warn("Conflict detected for POST /api/users/{}/addresses", testUser.getUserId());
    }

    @Test
    void testUpdateShippingAddress_Success() throws Exception {
        log.info("Testing PUT /api/users/{}/addresses/{addressId}", testUser.getUserId());

        ShippingAddress address = createTestShippingAddress(testUser, "123 Old Street");
        shippingAddressRepository.save(address);

        ShippingAddressRequestDTO requestDTO = new ShippingAddressRequestDTO(
                "456 Updated Street", "Updated City", "Updated State", "Updated Country", "67890"
        );

        mockMvc.perform(put("/api/users/{userId}/addresses/{addressId}", testUser.getUserId(), address.getAddressId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(jsonPath("$.street").value("456 Updated Street"))
                .andExpect(jsonPath("$.postalCode").value("67890"));

        log.info("Successfully tested PUT /api/users/{}/addresses/{}", testUser.getUserId(), address.getAddressId());
    }

    @Test
    void testDeleteShippingAddress_Success() throws Exception {
        log.info("Testing DELETE /api/users/{}/addresses/{addressId}", testUser.getUserId());

        ShippingAddress address = createTestShippingAddress(testUser, "123 Deletable Street");
        shippingAddressRepository.save(address);

        mockMvc.perform(delete("/api/users/{userId}/addresses/{addressId}", testUser.getUserId(), address.getAddressId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/{userId}/addresses/{addressId}", testUser.getUserId(), address.getAddressId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Shipping address not found"));

        log.info("Successfully tested DELETE /api/users/{}/addresses/{}", testUser.getUserId(), address.getAddressId());
    }

    @Test
    void testCreateShippingAddress_ValidationError() throws Exception {
        log.info("Testing POST /api/users/{}/addresses for validation error", testUser.getUserId());

        ShippingAddressRequestDTO requestDTO = new ShippingAddressRequestDTO(
                "", "New City", "New State", "New Country", "abcde" // Missing street, invalid postal code
        );

        mockMvc.perform(post("/api/users/{userId}/addresses", testUser.getUserId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.street").value("Street is required"))
                .andExpect(jsonPath("$.postalCode").value("Postal code must be a valid 4 or 5-digit format"));

        log.info("Validation error correctly handled for POST /api/users/{}/addresses", testUser.getUserId());
    }

    private ShippingAddress createTestShippingAddress(User user, String street) {
        ShippingAddress address = new ShippingAddress();
        address.setUser(user);
        address.setStreet(street);
        address.setCity("Test City");
        address.setState("Test State");
        address.setCountry("Test Country");
        address.setPostalCode("12345");
        return address;
    }
}
