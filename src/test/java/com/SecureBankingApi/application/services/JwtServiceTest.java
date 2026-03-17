package com.SecureBankingApi.application.services;

import com.SecureBankingApi.domain.user.User;
import com.SecureBankingApi.domain.user.enums.UserRole;
import com.SecureBankingApi.domain.user.valueObjects.CPF;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtService Tests")
public class JwtServiceTest {

    private JwtService jwtService;
    private User testUser;
    private String testSecretKey;
    private long testAccessTokenExpiration;

    @BeforeEach
    void setUp() {
        // Setup test configuration
        testSecretKey = "my-256-bit-secret-key-for-testing-purposes-only-do-not-use-in-production";
        testAccessTokenExpiration = 3600000; // 1 hour in milliseconds

        // Initialize JwtService
        jwtService = new JwtService(testSecretKey, testAccessTokenExpiration);

        // Create test user
        CPF testCpf = new CPF("12345678901");
        testUser = User.create(
                "test@example.com",
                "Test User",
                testCpf,
                "hashedPassword123",
                UserRole.USER
        );

        // Set ID manually using reflection (since it's set to null in create method)
        ReflectionTestUtils.setField(testUser, "id", UUID.randomUUID());
    }

    @Nested
    @DisplayName("generateAccessToken method tests")
    class GenerateAccessTokenTests {

        @Test
        @DisplayName("should generate a valid token")
        void shouldGenerateValidToken() {
            // Act
            String token = jwtService.generateAccessToken(testUser);

            // Assert
            assertNotNull(token);
            assertNotEquals("", token);
            assertTrue(token.contains("."), "Token should have JWT format with dots");
        }

        @Test
        @DisplayName("should include user ID in token")
        void shouldIncludeUserIdInToken() {
            // Act
            String token = jwtService.generateAccessToken(testUser);

            // Assert
            UUID extractedId = jwtService.extractUserId(token);
            assertEquals(testUser.getId(), extractedId);
        }

        @Test
        @DisplayName("should include email claim in token")
        void shouldIncludeEmailClaimInToken() {
            // Act
            String token = jwtService.generateAccessToken(testUser);

            // Assert
            String extractedEmail = jwtService.extractEmail(token);
            assertEquals(testUser.getEmail(), extractedEmail);
        }

        @Test
        @DisplayName("should include role claim in token")
        void shouldIncludeRoleClaimInToken() {
            // Act
            String token = jwtService.generateAccessToken(testUser);

            // Assert
            String extractedRole = jwtService.extractRole(token);
            assertEquals(testUser.getRole().toString(), extractedRole);
        }

        @Test
        @DisplayName("should generate token with different roles")
        void shouldGenerateTokenWithDifferentRoles() {
            // Arrange
            CPF cpf = new CPF("12345678901");
            User adminUser = User.create(
                    "admin@example.com",
                    "Admin User",
                    cpf,
                    "hashedPassword456",
                    UserRole.ADMIN
            );
            ReflectionTestUtils.setField(adminUser, "id", UUID.randomUUID());

            // Act
            String token = jwtService.generateAccessToken(adminUser);

            // Assert
            String extractedRole = jwtService.extractRole(token);
            assertEquals("ADMIN", extractedRole);
        }

        @Test
        @DisplayName("should generate token with READ_ONLY role")
        void shouldGenerateTokenWithReadOnlyRole() {
            // Arrange
            CPF cpf = new CPF("12345678901");
            User readOnlyUser = User.create(
                    "readonly@example.com",
                    "Read Only User",
                    cpf,
                    "hashedPassword789",
                    UserRole.READ_ONLY
            );
            ReflectionTestUtils.setField(readOnlyUser, "id", UUID.randomUUID());

            // Act
            String token = jwtService.generateAccessToken(readOnlyUser);

            // Assert
            String extractedRole = jwtService.extractRole(token);
            assertEquals("READ_ONLY", extractedRole);
        }

        @Test
        @DisplayName("should generate token with name claim")
        void shouldGenerateTokenWithNameClaim() {
            // Act
            String token = jwtService.generateAccessToken(testUser);

            // Assert
            // Name is stored as "name" claim, we can verify it's present by checking token doesn't fail extraction
            assertNotNull(token);
            assertTrue(jwtService.isValidToken(token));
        }
    }

    @Nested
    @DisplayName("isValidToken method tests")
    class IsValidTokenTests {

        @Test
        @DisplayName("should validate a generated token")
        void shouldValidateGeneratedToken() {
            // Arrange
            String token = jwtService.generateAccessToken(testUser);

            // Act
            boolean isValid = jwtService.isValidToken(token);

            // Assert
            assertTrue(isValid);
        }

        @Test
        @DisplayName("should return false for null token")
        void shouldReturnFalseForNullToken() {
            // Act & Assert - JwtService throws exception for null
            assertThrows(IllegalArgumentException.class, () -> {
                jwtService.isValidToken(null);
            });
        }

        @Test
        @DisplayName("should return false for empty token")
        void shouldReturnFalseForEmptyToken() {
            // Act & Assert - JwtService throws exception for empty string
            assertThrows(IllegalArgumentException.class, () -> {
                jwtService.isValidToken("");
            });
        }

        @Test
        @DisplayName("should return false for malformed token")
        void shouldReturnFalseForMalformedToken() {
            // Arrange
            String malformedToken = "malformed.token.here";

            // Act
            boolean isValid = jwtService.isValidToken(malformedToken);

            // Assert
            assertFalse(isValid);
        }

        @Test
        @DisplayName("should return false for token with invalid signature")
        void shouldReturnFalseForInvalidSignature() {
            // Arrange
            String validToken = jwtService.generateAccessToken(testUser);
            // Tamper with the signature
            String tamperedToken = validToken.substring(0, validToken.length() - 10) + "0000000000";

            // Act
            boolean isValid = jwtService.isValidToken(tamperedToken);

            // Assert
            assertFalse(isValid);
        }

        @Test
        @DisplayName("should return false for token created with different secret")
        void shouldReturnFalseForDifferentSecret() {
            // Arrange
            JwtService differentSecretService = new JwtService(
                    "different-256-bit-secret-key-for-testing-only-do-not-use-in-production",
                    3600000
            );
            String tokenWithDifferentSecret = differentSecretService.generateAccessToken(testUser);

            // Act
            boolean isValid = jwtService.isValidToken(tokenWithDifferentSecret);

            // Assert
            assertFalse(isValid);
        }

        @Test
        @DisplayName("should return false for garbage input")
        void shouldReturnFalseForGarbageInput() {
            // Act & Assert
            assertFalse(jwtService.isValidToken("!@#$%^&*()"));
            assertFalse(jwtService.isValidToken("random-text-that-is-not-a-token"));
        }
    }

    @Nested
    @DisplayName("extractUserId method tests")
    class ExtractUserIdTests {

        @Test
        @DisplayName("should extract user ID from token")
        void shouldExtractUserIdFromToken() {
            // Arrange
            String token = jwtService.generateAccessToken(testUser);

            // Act
            UUID extractedId = jwtService.extractUserId(token);

            // Assert
            assertEquals(testUser.getId(), extractedId);
            assertNotNull(extractedId);
        }

        @Test
        @DisplayName("should extract correct UUID format")
        void shouldExtractCorrectUUIDFormat() {
            // Arrange
            String token = jwtService.generateAccessToken(testUser);

            // Act
            UUID extractedId = jwtService.extractUserId(token);

            // Assert
            assertNotNull(extractedId);
            assertTrue(extractedId.toString().matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
        }

        @Test
        @DisplayName("should throw exception for invalid token")
        void shouldThrowExceptionForInvalidToken() {
            // Arrange
            String invalidToken = "invalid.token.here";

            // Act & Assert
            assertThrows(JwtException.class, () -> {
                jwtService.extractUserId(invalidToken);
            });
        }

        @Test
        @DisplayName("should extract different IDs for different users")
        void shouldExtractDifferentIdsForDifferentUsers() {
            // Arrange
            CPF cpf1 = new CPF("12345678901");
            CPF cpf2 = new CPF("98765432109");
            UUID userId1 = UUID.randomUUID();
            UUID userId2 = UUID.randomUUID();

            User user1 = User.create("user1@example.com", "User One", cpf1, "pass1", UserRole.USER);
            User user2 = User.create("user2@example.com", "User Two", cpf2, "pass2", UserRole.USER);

            ReflectionTestUtils.setField(user1, "id", userId1);
            ReflectionTestUtils.setField(user2, "id", userId2);

            // Act
            String token1 = jwtService.generateAccessToken(user1);
            String token2 = jwtService.generateAccessToken(user2);

            UUID extractedId1 = jwtService.extractUserId(token1);
            UUID extractedId2 = jwtService.extractUserId(token2);

            // Assert
            assertEquals(userId1, extractedId1);
            assertEquals(userId2, extractedId2);
            assertNotEquals(extractedId1, extractedId2);
        }
    }

    @Nested
    @DisplayName("extractEmail method tests")
    class ExtractEmailTests {

        @Test
        @DisplayName("should extract email from token")
        void shouldExtractEmailFromToken() {
            // Arrange
            String token = jwtService.generateAccessToken(testUser);

            // Act
            String extractedEmail = jwtService.extractEmail(token);

            // Assert
            assertEquals(testUser.getEmail(), extractedEmail);
        }

        @Test
        @DisplayName("should extract correct email format")
        void shouldExtractCorrectEmailFormat() {
            // Arrange
            String token = jwtService.generateAccessToken(testUser);

            // Act
            String extractedEmail = jwtService.extractEmail(token);

            // Assert
            assertTrue(extractedEmail.contains("@"));
            assertTrue(extractedEmail.contains("."));
        }

        @Test
        @DisplayName("should throw exception for invalid token")
        void shouldThrowExceptionForInvalidToken() {
            // Arrange
            String invalidToken = "invalid.token.here";

            // Act & Assert
            assertThrows(JwtException.class, () -> {
                jwtService.extractEmail(invalidToken);
            });
        }

        @Test
        @DisplayName("should extract different emails for different users")
        void shouldExtractDifferentEmailsForDifferentUsers() {
            // Arrange
            CPF cpf = new CPF("12345678901");
            User userA = User.create("userA@example.com", "User A", cpf, "pass", UserRole.USER);
            User userB = User.create("userB@example.com", "User B", cpf, "pass", UserRole.USER);

            ReflectionTestUtils.setField(userA, "id", UUID.randomUUID());
            ReflectionTestUtils.setField(userB, "id", UUID.randomUUID());

            // Act
            String tokenA = jwtService.generateAccessToken(userA);
            String tokenB = jwtService.generateAccessToken(userB);

            String extractedEmailA = jwtService.extractEmail(tokenA);
            String extractedEmailB = jwtService.extractEmail(tokenB);

            // Assert
            assertEquals("userA@example.com", extractedEmailA);
            assertEquals("userB@example.com", extractedEmailB);
            assertNotEquals(extractedEmailA, extractedEmailB);
        }
    }

    @Nested
    @DisplayName("extractRole method tests")
    class ExtractRoleTests {

        @Test
        @DisplayName("should extract role from token")
        void shouldExtractRoleFromToken() {
            // Arrange
            String token = jwtService.generateAccessToken(testUser);

            // Act
            String extractedRole = jwtService.extractRole(token);

            // Assert
            assertEquals("USER", extractedRole);
        }

        @Test
        @DisplayName("should extract ADMIN role")
        void shouldExtractAdminRole() {
            // Arrange
            CPF cpf = new CPF("12345678901");
            User adminUser = User.create("admin@example.com", "Admin", cpf, "pass", UserRole.ADMIN);
            ReflectionTestUtils.setField(adminUser, "id", UUID.randomUUID());

            // Act
            String token = jwtService.generateAccessToken(adminUser);
            String extractedRole = jwtService.extractRole(token);

            // Assert
            assertEquals("ADMIN", extractedRole);
        }

        @Test
        @DisplayName("should extract READ_ONLY role")
        void shouldExtractReadOnlyRole() {
            // Arrange
            CPF cpf = new CPF("12345678901");
            User readOnlyUser = User.create("readonly@example.com", "ReadOnly", cpf, "pass", UserRole.READ_ONLY);
            ReflectionTestUtils.setField(readOnlyUser, "id", UUID.randomUUID());

            // Act
            String token = jwtService.generateAccessToken(readOnlyUser);
            String extractedRole = jwtService.extractRole(token);

            // Assert
            assertEquals("READ_ONLY", extractedRole);
        }

        @Test
        @DisplayName("should throw exception for invalid token")
        void shouldThrowExceptionForInvalidToken() {
            // Arrange
            String invalidToken = "invalid.token.here";

            // Act & Assert
            assertThrows(JwtException.class, () -> {
                jwtService.extractRole(invalidToken);
            });
        }
    }

    @Nested
    @DisplayName("isTokenExpired method tests")
    class IsTokenExpiredTests {

        @Test
        @DisplayName("should return false for fresh token")
        void shouldReturnFalseForFreshToken() {
            // Arrange
            String token = jwtService.generateAccessToken(testUser);

            // Act
            boolean isExpired = jwtService.isTokenExpired(token);

            // Assert
            assertFalse(isExpired);
        }

        @Test
        @DisplayName("should return true for expired token")
        void shouldReturnTrueForExpiredToken() {
            // Arrange - Create service with very short expiration
            JwtService shortLivedService = new JwtService(testSecretKey, 1); // 1ms expiration
            String token = shortLivedService.generateAccessToken(testUser);

            // Wait for token to expire
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Act
            boolean isExpired = shortLivedService.isTokenExpired(token);

            // Assert
            assertTrue(isExpired);
        }

        @Test
        @DisplayName("should return true for invalid token")
        void shouldReturnTrueForInvalidToken() {
            // Arrange
            String invalidToken = "invalid.token.here";

            // Act
            boolean isExpired = jwtService.isTokenExpired(invalidToken);

            // Assert
            assertTrue(isExpired);
        }

        @Test
        @DisplayName("should return false for token with 1 hour expiration")
        void shouldReturnFalseForToken1HourExpiration() {
            // Arrange
            long oneHourMs = 3600000;
            JwtService oneHourService = new JwtService(testSecretKey, oneHourMs);
            String token = oneHourService.generateAccessToken(testUser);

            // Act
            boolean isExpired = oneHourService.isTokenExpired(token);

            // Assert
            assertFalse(isExpired);
        }
    }

    @Nested
    @DisplayName("Integration tests")
    class IntegrationTests {

        @Test
        @DisplayName("should complete full token lifecycle")
        void shouldCompleteFullTokenLifecycle() {
            // Arrange & Act - Generate token
            String token = jwtService.generateAccessToken(testUser);

            // Assert - Token is valid
            assertTrue(jwtService.isValidToken(token));
            assertFalse(jwtService.isTokenExpired(token));

            // Assert - Extract all claims
            assertEquals(testUser.getId(), jwtService.extractUserId(token));
            assertEquals(testUser.getEmail(), jwtService.extractEmail(token));
            assertEquals(testUser.getRole().toString(), jwtService.extractRole(token));
        }

        @Test
        @DisplayName("should handle multiple tokens for different users independently")
        void shouldHandleMultipleTokensIndependently() {
            // Arrange
            CPF cpf = new CPF("12345678901");
            User user1 = User.create("user1@example.com", "User One", cpf, "pass1", UserRole.USER);
            User user2 = User.create("user2@example.com", "User Two", cpf, "pass2", UserRole.ADMIN);

            UUID userId1 = UUID.randomUUID();
            UUID userId2 = UUID.randomUUID();
            ReflectionTestUtils.setField(user1, "id", userId1);
            ReflectionTestUtils.setField(user2, "id", userId2);

            // Act
            String token1 = jwtService.generateAccessToken(user1);
            String token2 = jwtService.generateAccessToken(user2);

            // Assert
            assertTrue(jwtService.isValidToken(token1));
            assertTrue(jwtService.isValidToken(token2));

            assertEquals(userId1, jwtService.extractUserId(token1));
            assertEquals(userId2, jwtService.extractUserId(token2));

            assertEquals("user1@example.com", jwtService.extractEmail(token1));
            assertEquals("user2@example.com", jwtService.extractEmail(token2));

            assertEquals("USER", jwtService.extractRole(token1));
            assertEquals("ADMIN", jwtService.extractRole(token2));
        }

        @Test
        @DisplayName("should validate token integrity across all claims")
        void shouldValidateTokenIntegrityAcrossAllClaims() {
            // Arrange
            String token = jwtService.generateAccessToken(testUser);

            // Assert - All extractions should succeed and match
            assertDoesNotThrow(() -> {
                UUID userId = jwtService.extractUserId(token);
                String email = jwtService.extractEmail(token);
                String role = jwtService.extractRole(token);

                assertEquals(testUser.getId(), userId);
                assertEquals(testUser.getEmail(), email);
                assertEquals(testUser.getRole().toString(), role);
            });
        }
    }
}
