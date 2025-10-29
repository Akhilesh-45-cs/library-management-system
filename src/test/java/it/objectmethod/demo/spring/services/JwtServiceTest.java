package it.objectmethod.demo.spring.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.JWT;

import it.objectmethod.demo.spring.models.UserObject;

/**
 * Unit tests for JwtService.
 *
 * These tests instantiate JwtService directly (no Spring context) because JwtService
 * has no external dependencies.
 */
public class JwtServiceTest {

    private JwtService jwtService;

    private UserObject testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        testUser = new UserObject();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setRole("user");
    }

    @Test
    void testCreateAndCheckToken() {
        // Act
        String token = jwtService.createJWTToken(testUser);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(jwtService.checkJWTToken(token));
    }

    @Test
    void testCheckToken_InvalidToken() {
        // Act & Assert
        assertFalse(jwtService.checkJWTToken("invalid.token.here"));
    }

    @Test
    void testDecodeTokenClaims() {
        // Arrange
        String token = jwtService.createJWTToken(testUser);

        // Act
        DecodedJWT decoded = JWT.decode(token);
        Long id = decoded.getClaim("id").asLong();
        String username = decoded.getClaim("username").asString();

        // Assert
        assertEquals(1L, id.longValue());
        assertEquals("testuser", username);
    }

    @Test
    void testCheckToken_emptyOrNull_returnsFalse() {
        assertFalse(jwtService.checkJWTToken(""));
        assertFalse(jwtService.checkJWTToken(null));
    }

}