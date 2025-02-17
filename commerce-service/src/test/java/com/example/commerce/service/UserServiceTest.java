package com.example.commerce.service;

import com.example.commerce.dto.UserRequestDTO;
import com.example.commerce.dto.UserResponseDTO;
import com.example.commerce.model.User;
import com.example.commerce.model.enums.Role;
import com.example.commerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private User user;
    private UserRequestDTO requestDTO;

    @BeforeEach
    void setup() {
        user = new User();
        user.setName("Onyx");
        user.setEmail("onyx@corp.com");
        user.setPassword("password12345");
        user.setRole(Role.CUSTOMER);
        userRepository.save(user);
        userId = user.getUserId();

        requestDTO = new UserRequestDTO(
                "Onyx", "onyx@corp.com", "securepassword", Role.CUSTOMER
        );
    }

    /**
     * Test successful user creation
     * - Mocks repository to return false for `existsByEmail()`
     * - Ensures user is saved correctly
     */
    @Test
    void testCreateUser_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO createdUser = userService.createUser(requestDTO);

        assertNotNull(createdUser);
        assertEquals(requestDTO.getEmail(), createdUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Test user creation failure due to duplicate email
     * - Mocks repository to return true for `existsByEmail()`
     * - Ensures exception is thrown
     */
    @Test
    void testCreateUser_DuplicateEmail() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.createUser(requestDTO);
        });

        assertEquals("409 CONFLICT \"User already exists\"", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Test fetching a user by ID (User Exists)
     * - Mocks repository to return an existing user
     */
    @Test
    void testGetUserById_Exists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponseDTO foundUser = userService.getUserById(userId);

        assertNotNull(foundUser);
        assertEquals(user.getUserId(), foundUser.getUserId());
        assertEquals(user.getEmail(), foundUser.getEmail());
    }

    /**
     * Test deleting a user (User Exists)
     * - Ensures `deleteById()` is called once
     */
    @Test
    void testDeleteUser_Exists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).delete(user);
    }
}
