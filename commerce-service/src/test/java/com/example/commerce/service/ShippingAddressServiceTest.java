package com.example.commerce.service;

import com.example.commerce.dto.ShippingAddressRequestDTO;
import com.example.commerce.dto.ShippingAddressResponseDTO;
import com.example.commerce.model.ShippingAddress;
import com.example.commerce.model.User;
import com.example.commerce.repository.ShippingAddressRepository;
import com.example.commerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShippingAddressServiceTest {

    @Mock
    private ShippingAddressRepository shippingAddressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ShippingAddressService shippingAddressService;

    @Test
    void testCreateShippingAddress_Success() {
        UUID userId = UUID.randomUUID();
        ShippingAddressRequestDTO requestDTO = new ShippingAddressRequestDTO(
                "Hauptstraße 10", "Berlin", "Berlin", "Germany", "10115"
        );

        User user = new User();
        user.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shippingAddressRepository.save(any(ShippingAddress.class))).thenAnswer(i -> i.getArgument(0));

        ShippingAddressResponseDTO responseDTO = shippingAddressService.createShippingAddress(userId, requestDTO);

        assertNotNull(responseDTO);
        assertEquals("Hauptstraße 10", responseDTO.getStreet());
        verify(shippingAddressRepository, times(1)).save(any(ShippingAddress.class));
    }

    @Test
    void testCreateShippingAddress_UserNotFound() {
        UUID userId = UUID.randomUUID();
        ShippingAddressRequestDTO requestDTO = new ShippingAddressRequestDTO(
                "Hauptstraße 10", "Berlin", "Berlin", "Germany", "10115"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> shippingAddressService.createShippingAddress(userId, requestDTO));

        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    void testUpdateShippingAddress_Success() {
        UUID addressId = UUID.randomUUID();
        ShippingAddress existingAddress = new ShippingAddress(
                addressId, null, "Hauptstraße 10", "Berlin", "Berlin", "Germany", "10115", LocalDateTime.now(), LocalDateTime.now()
        );

        ShippingAddressRequestDTO requestDTO = new ShippingAddressRequestDTO(
                "Neue Straße 5", "Hamburg", "Hamburg", "Germany", "21073"
        );

        when(shippingAddressRepository.findById(addressId)).thenReturn(Optional.of(existingAddress));
        when(shippingAddressRepository.save(any(ShippingAddress.class))).thenAnswer(i -> i.getArgument(0));

        ShippingAddressResponseDTO responseDTO = shippingAddressService.updateShippingAddress(addressId, requestDTO);

        assertNotNull(responseDTO);
        assertEquals("Neue Straße 5", responseDTO.getStreet());
        assertEquals("Hamburg", responseDTO.getCity());
        assertEquals("Hamburg", responseDTO.getState());
        assertEquals("Germany", responseDTO.getCountry());
        assertEquals("21073", responseDTO.getPostalCode());
        verify(shippingAddressRepository, times(1)).save(any(ShippingAddress.class));
    }

    @Test
    void testUpdateShippingAddress_NotFound() {
        UUID addressId = UUID.randomUUID();
        ShippingAddressRequestDTO requestDTO = new ShippingAddressRequestDTO(
                "Neue Straße 5", "Hamburg", "Hamburg", "Germany", "21073"
        );

        when(shippingAddressRepository.findById(addressId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> shippingAddressService.updateShippingAddress(addressId, requestDTO));

        assertTrue(exception.getMessage().contains("Shipping address not found"));
    }
}
