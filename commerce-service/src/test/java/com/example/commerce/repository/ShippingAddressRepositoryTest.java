package com.example.commerce.repository;

import com.example.commerce.model.ShippingAddress;
import com.example.commerce.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
public class ShippingAddressRepositoryTest {

    @Autowired
    private ShippingAddressRepository shippingAddressRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUserUserId() {
        User user = new User();
        user.setEmail("onyx@corp.com");
        userRepository.save(user);

        ShippingAddress address1 = new ShippingAddress();
        address1.setUser(user);
        address1.setStreet("123 Main St");
        address1.setCity("Test City");
        address1.setCountry("Test Country");
        shippingAddressRepository.save(address1);

        ShippingAddress address2 = new ShippingAddress();
        address2.setUser(user);
        address2.setStreet("456 Another St");
        address2.setCity("Another City");
        address2.setCountry("Another Country");
        shippingAddressRepository.save(address2);

        List<ShippingAddress> addresses = shippingAddressRepository.findByUserUserId(user.getUserId());
        assertEquals(2, addresses.size());
    }
}
