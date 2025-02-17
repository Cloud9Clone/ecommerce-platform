package com.example.commerce.service;

import com.example.commerce.dto.ShippingAddressRequestDTO;
import com.example.commerce.dto.ShippingAddressResponseDTO;
import com.example.commerce.model.ShippingAddress;
import com.example.commerce.model.User;
import com.example.commerce.repository.ShippingAddressRepository;
import com.example.commerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingAddressService {

    private final ShippingAddressRepository shippingAddressRepository;
    private final UserRepository userRepository;

    public List<ShippingAddressResponseDTO> getShippingAddressesForUser(UUID userId) {
        log.info("Fetching all shipping addresses for user withID: {}", userId);

        List<ShippingAddressResponseDTO> addresses = shippingAddressRepository.findByUserUserId(userId)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();

        log.info("Retrieved {} shipping addresses for user ID: {}", addresses.size(), userId);
        return addresses;
    }

    public ShippingAddressResponseDTO getShippingAddressById(UUID addressId) {
        log.info("Fetching shipping address with ID: {}", addressId);

        ShippingAddress address = shippingAddressRepository.findById(addressId)
                .orElseThrow(() -> {
                    log.error("Shipping address with ID {} not found", addressId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipping address not found");
                });

        log.debug("Successfully fetched shipping address: {}", address);
        return mapToResponseDTO(address);
    }

    public ShippingAddressResponseDTO createShippingAddress(UUID userId, ShippingAddressRequestDTO requestDTO) {
        log.info("Creating shipping address for user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found", userId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                });

        log.debug("User details: {}", user);

        boolean addressExists = shippingAddressRepository.existsByUserAndStreetAndCityAndStateAndCountryAndPostalCode(
                user, requestDTO.getStreet().trim(), requestDTO.getCity().trim(), requestDTO.getState().trim(), requestDTO.getCountry().trim(), requestDTO.getPostalCode().trim()
        );

        if (addressExists) {
            log.warn("Duplicate address for user ID: {}", userId);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Shipping address already exists for this user");
        }

        ShippingAddress address = new ShippingAddress();
        address.setUser(user);
        address.setStreet(requestDTO.getStreet().trim());
        address.setCity(requestDTO.getCity().trim());
        address.setState(requestDTO.getState().trim());
        address.setCountry(requestDTO.getCountry().trim());
        address.setPostalCode(requestDTO.getPostalCode().trim());

        ShippingAddress savedAddress = shippingAddressRepository.save(address);
        log.info("Successfully created shipping address with ID: {}", savedAddress.getAddressId());
        return mapToResponseDTO(savedAddress);
    }

    public ShippingAddressResponseDTO updateShippingAddress(UUID addressId, ShippingAddressRequestDTO requestDTO) {
        log.info("Updating shipping address with ID: {}", addressId);

        ShippingAddress existingAddress = shippingAddressRepository.findById(addressId)
                .orElseThrow(() -> {
                    log.error("Shipping address with ID {} not found", addressId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipping address not found");
                });

        log.debug("Existing address before update: {}", existingAddress);

        // Trim nad normalize the input fields
        String newStreet = requestDTO.getStreet().trim();
        String newCity = requestDTO.getCity().trim();
        String newState = requestDTO.getState().trim();
        String newCountry = requestDTO.getCountry().trim();
        String newPostalCode = requestDTO.getPostalCode().trim();

        // Check if any field has changed
        boolean hasChanges = !(newStreet.equals(existingAddress.getStreet()) &&
                                newCity.equals(existingAddress.getCity()) &&
                                newState.equals(existingAddress.getState()) &&
                                newCountry.equals(existingAddress.getCountry()) &&
                                newPostalCode.equals(existingAddress.getPostalCode()));

        if (!hasChanges) {
            log.info("No changes detected for shipping address with ID: {}", addressId);
            return mapToResponseDTO(existingAddress);
        }

        // Update fields based on the request DTO
        existingAddress.setStreet(newStreet);
        existingAddress.setCity(newCity);
        existingAddress.setState(newState);
        existingAddress.setCountry(newCountry);
        existingAddress.setPostalCode(newPostalCode);

        ShippingAddress updatedAddress = shippingAddressRepository.save(existingAddress);
        log.debug("Saving updated shipping address with ID: {}", addressId);
        return mapToResponseDTO(updatedAddress);
    }

    public void deleteShippingAddress(UUID addressId) {
        log.info("Deleting shipping address with ID: {}", addressId);

        if (!shippingAddressRepository.existsById(addressId)) {
            log.error("Shipping address with ID {} not found", addressId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipping address not found");
        }
        shippingAddressRepository.deleteById(addressId);
        log.info("Successfully deleted shipping address with ID: {}", addressId);
    }

    // Helper method to convert ShippingAddress to ShippingAddressResponseDTO
    private ShippingAddressResponseDTO mapToResponseDTO(ShippingAddress address) {
        log.debug("Mapping ShippingAddress entity to DTO for address ID: {}", address.getAddressId());
        return new ShippingAddressResponseDTO(
                address.getAddressId(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getCountry(),
                address.getPostalCode(),
                address.getCreatedAt()
        );
    }
}
