package com.valr.orderbook.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    public void setup() {
        jwtUtil = new JwtUtil();
    }

    @Test
    public void generateToken_with_valid_username_returns_token() {
        String token = jwtUtil.generateToken("validUser");
        assertNotNull(token);
    }

    @Test
    public void validateToken_with_valid_token_returns_true() {
        String token = jwtUtil.generateToken("validUser");
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    public void validateToken_with_invalid_token_returns_false() {
        String invalidToken = "invalidToken";
        assertFalse(jwtUtil.validateToken(invalidToken));
    }

    @Test
    public void extractUsername_with_valid_token_returns_username() {
        String token = jwtUtil.generateToken("validUser");
        String username = jwtUtil.extractUsername(token);
        assertEquals("validUser", username);
    }

    @Test
    public void extractUsername_with_invalid_token_throws_exception() {
        String invalidToken = "invalidToken";
        assertThrows(Exception.class, () -> jwtUtil.extractUsername(invalidToken));
    }

    @Test
    public void generateToken_with_empty_username_returns_token() {
        String token = jwtUtil.generateToken("");
        assertNotNull(token);
    }
}