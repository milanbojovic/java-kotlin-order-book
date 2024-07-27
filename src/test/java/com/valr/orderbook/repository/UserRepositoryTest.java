package com.valr.orderbook.repository;

import com.valr.orderbook.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
        userRepository.insertData();
    }

    @Test
    void login_with_valid_credentials_returns_user() {
        Optional<User> user = userRepository.login("admin", "admin");
        assertTrue(user.isPresent());
        assertEquals("admin", user.get().getUsername());
    }

    @Test
    void login_with_invalid_username_returns_empty() {
        Optional<User> user = userRepository.login("invalid", "admin");
        assertFalse(user.isPresent());
    }

    @Test
    void login_with_invalid_case_sensitive_username_returns_empty() {
        Optional<User> user = userRepository.login("AdmiN", "ADMIN");
        assertFalse(user.isPresent());
    }

    @Test
    void login_with_invalid_password_returns_empty() {
        Optional<User> user = userRepository.login("admin", "invalid");
        assertFalse(user.isPresent());
    }

    @Test
    void login_with_null_username_returns_empty() {
        Optional<User> user = userRepository.login(null, "admin");
        assertFalse(user.isPresent());
    }

    @Test
    void login_with_null_password_returns_empty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userRepository.login("admin", null));

        assertEquals("rawPassword cannot be null", exception.getMessage());
    }

    @Test
    void login_with_empty_username_returns_empty() {
        Optional<User> user = userRepository.login("", "admin");
        assertFalse(user.isPresent());
    }

    @Test
    void login_with_empty_password_returns_empty() {
        Optional<User> user = userRepository.login("admin", "");
        assertFalse(user.isPresent());
    }
}