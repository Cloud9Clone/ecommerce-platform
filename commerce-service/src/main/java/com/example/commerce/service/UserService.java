package com.example.commerce.service;

import com.example.commerce.dto.UserRequestDTO;
import com.example.commerce.dto.UserResponseDTO;
import com.example.commerce.model.User;
import com.example.commerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDTO getUserById(UUID userId) {
        log.info("Retrieving User with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                   log.error("User ID {} not found", userId);
                   return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                });
        return mapToResponseDTO(user);
    }

    public List<UserResponseDTO> getAllUsers() {
        log.info("Retrieving all users");
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        log.info("Creating a new user with email: {}", requestDTO.getEmail());

        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            log.error("User with email {} already exists", requestDTO.getEmail());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");
        }

        User user = new User();
        user.setName(requestDTO.getName());
        user.setEmail(requestDTO.getEmail());
        user.setPassword(requestDTO.getPassword()); // For production, hash it!
        user.setRole(requestDTO.getRole());

        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getUserId());

        return mapToResponseDTO(savedUser);
    }

    public UserResponseDTO updateUser(UUID userId, UserRequestDTO requestDTO) {
        log.info("Updating user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                   log.error("User ID {} not found", userId);
                   return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                });

        user.setName(requestDTO.getName());
        user.setEmail(requestDTO.getEmail());
        user.setPassword(requestDTO.getPassword()); // Should be hashed
        user.setRole(requestDTO.getRole());

        User updatedUser = userRepository.save(user);
        log.info("User with ID {} updated successfully", userId);

        return mapToResponseDTO(updatedUser);
    }

    public void deleteUser(UUID userId) {
        log.info("Deleting user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User ID {} not found", userId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                });

        userRepository.delete(user);
        log.info("User with ID {} deleted successfully", userId);
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
