package com.example.commerce.repository;

import com.example.commerce.model.ShippingAddress;
import com.example.commerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, UUID> {
    List<ShippingAddress> findByUserUserId(UUID userId);
    boolean existsByUserAndStreetAndCityAndStateAndCountryAndPostalCode(User user, String street, String city, String state, String country, String postalCode);
}
