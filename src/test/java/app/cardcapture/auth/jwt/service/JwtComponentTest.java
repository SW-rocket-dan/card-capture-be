package app.cardcapture.auth.jwt.service;

import app.cardcapture.auth.jwt.domain.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

class JwtComponentTest {

    private JwtComponent jwtComponent;

    @BeforeEach
    void setUp() {
        jwtComponent = JwtComponentStub.createStub();
    }

    @Test
    void testCreateToken() {
        // Given
        Long userId = 12345789L;
        String role = "USER";
        Date createdAt = new Date();

        // When
        String token = jwtComponent.create(userId, role, createdAt);

        // Then
        assertNotNull(token);
    }

    @Test
    void testVerifyToken() {
        // Given
        Claims claims = Claims.of(12345789L, "USER", "test-issuer", new Date());
        String token = jwtComponent.create(claims);

        // When
        Claims verifiedClaims = jwtComponent.verify(token);

        // Then
        assertAll("Verify decoded claims",
                () -> assertEquals(claims.getId(), verifiedClaims.getId()),
                () -> assertArrayEquals(claims.getRoles(), verifiedClaims.getRoles()),
                () -> assertEquals(claims.getIssuer(), verifiedClaims.getIssuer())
        );
    }

    @Test
    void testVerifyInvalidToken() {
        // Given
        String invalidToken = "invalid-token";

        // When & Then
        assertThatThrownBy(() -> jwtComponent.verify(invalidToken))
                .isInstanceOf(com.auth0.jwt.exceptions.JWTVerificationException.class);
    }
}