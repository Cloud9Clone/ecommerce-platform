package com.example.commerce.repository;

import com.example.commerce.model.User;
import com.example.commerce.model.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest // Can be extended with TestContainers
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByEmail() {
        User user = new User();
        user.setName("Navinor");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail("test@example.com");
        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }
}
