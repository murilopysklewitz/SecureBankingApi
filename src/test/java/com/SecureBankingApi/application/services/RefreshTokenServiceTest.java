package com.SecureBankingApi.application.services;

import com.SecureBankingApi.application.exceptions.InvalidRefreshToken;
import com.SecureBankingApi.domain.refreshToken.RefreshToken;
import com.SecureBankingApi.domain.refreshToken.RefreshTokenRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("RefreshTokenService Tests")
@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository repository;

    private RefreshTokenService refreshTokenService;

    private UUID testUserId;
    private long testRefreshTokenExpiration;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testRefreshTokenExpiration = 604800000; // 7 days in milliseconds
        
        // Create instance manually since RefreshTokenService requires constructor parameters
        refreshTokenService = new RefreshTokenService(repository, testRefreshTokenExpiration);
    }

    @Nested
    @DisplayName("generateRefreshToken method tests")
    class GenerateRefreshTokenTests {

        @Test
        @DisplayName("should generate a refresh token")
        void shouldGenerateRefreshToken() {
            // Arrange
            doNothing().when(repository).save(any(RefreshToken.class));

            // Act
            RefreshToken token = refreshTokenService.generateRefreshToken(testUserId);

            // Assert
            assertNotNull(token);
            assertNotNull(token.getToken());
            assertEquals(testUserId, token.getUserId());
            assertFalse(token.isRevoked());
        }

        @Test
        @DisplayName("should generate token with unique UUID")
        void shouldGenerateTokenWithUniqueUUID() {
            // Arrange
            doNothing().when(repository).save(any(RefreshToken.class));

            // Act
            RefreshToken token1 = refreshTokenService.generateRefreshToken(testUserId);
            RefreshToken token2 = refreshTokenService.generateRefreshToken(testUserId);

            // Assert
            assertNotEquals(token1.getToken(), token2.getToken(), "Each generated token should be unique");
        }

        @Test
        @DisplayName("should set expiration time correctly")
        void shouldSetExpirationTimeCorrectly() {
            // Arrange
            doNothing().when(repository).save(any(RefreshToken.class));
            LocalDateTime beforeGeneration = LocalDateTime.now();

            // Act
            RefreshToken token = refreshTokenService.generateRefreshToken(testUserId);
            LocalDateTime afterGeneration = LocalDateTime.now();

            // Assert
            assertNotNull(token.getExpiresAt());
            assertTrue(token.getExpiresAt().isAfter(beforeGeneration));
            assertTrue(token.getExpiresAt().isAfter(afterGeneration));
            // Verify expiration is approximately 7 days (with small margin)
            long expectedDaysInSeconds = testRefreshTokenExpiration / 1000;
            long actualDaysInSeconds = java.time.temporal.ChronoUnit.SECONDS.between(
                    token.getCreatedAt(), 
                    token.getExpiresAt()
            );
            assertTrue(Math.abs(actualDaysInSeconds - expectedDaysInSeconds) < 5, 
                    "Expiration should be approximately " + expectedDaysInSeconds + " seconds");
        }

        @Test
        @DisplayName("should save token to repository")
        void shouldSaveTokenToRepository() {
            // Arrange
            doNothing().when(repository).save(any(RefreshToken.class));

            // Act
            RefreshToken token = refreshTokenService.generateRefreshToken(testUserId);

            // Assert
            ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
            verify(repository, times(1)).save(captor.capture());
            
            RefreshToken savedToken = captor.getValue();
            assertEquals(token.getToken(), savedToken.getToken());
            assertEquals(token.getUserId(), savedToken.getUserId());
        }

        @Test
        @DisplayName("should generate token not revoked initially")
        void shouldGenerateTokenNotRevokedInitially() {
            // Arrange
            doNothing().when(repository).save(any(RefreshToken.class));

            // Act
            RefreshToken token = refreshTokenService.generateRefreshToken(testUserId);

            // Assert
            assertFalse(token.isRevoked());
        }

        @Test
        @DisplayName("should generate token for different users")
        void shouldGenerateTokenForDifferentUsers() {
            // Arrange
            UUID userId1 = UUID.randomUUID();
            UUID userId2 = UUID.randomUUID();
            doNothing().when(repository).save(any(RefreshToken.class));

            // Act
            RefreshToken token1 = refreshTokenService.generateRefreshToken(userId1);
            RefreshToken token2 = refreshTokenService.generateRefreshToken(userId2);

            // Assert
            assertEquals(userId1, token1.getUserId());
            assertEquals(userId2, token2.getUserId());
            assertNotEquals(token1.getToken(), token2.getToken());
        }
    }

    @Nested
    @DisplayName("validateRefreshToken method tests")
    class ValidateRefreshTokenTests {

        @Test
        @DisplayName("should validate a valid refresh token")
        void shouldValidateValidRefreshToken() {
            // Arrange
            LocalDateTime futureTime = LocalDateTime.now().plusDays(7);
            RefreshToken validToken = RefreshToken.restore(
                    UUID.randomUUID(),
                    "valid-token",
                    testUserId,
                    false,
                    LocalDateTime.now(),
                    futureTime
            );

            when(repository.findByToken("valid-token")).thenReturn(Optional.of(validToken));

            // Act
            RefreshToken result = refreshTokenService.validateRefreshToken("valid-token");

            // Assert
            assertNotNull(result);
            assertEquals("valid-token", result.getToken());
            assertEquals(testUserId, result.getUserId());
            assertFalse(result.isRevoked());
        }

        @Test
        @DisplayName("should throw exception for non-existent token")
        void shouldThrowExceptionForNonExistentToken() {
            // Arrange
            when(repository.findByToken("non-existent-token")).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(InvalidRefreshToken.class, () -> {
                refreshTokenService.validateRefreshToken("non-existent-token");
            }, "Should throw InvalidRefreshToken when token not found");

            verify(repository, times(1)).findByToken("non-existent-token");
        }

        @Test
        @DisplayName("should throw exception for revoked token")
        void shouldThrowExceptionForRevokedToken() {
            // Arrange
            LocalDateTime futureTime = LocalDateTime.now().plusDays(7);
            RefreshToken revokedToken = RefreshToken.restore(
                    UUID.randomUUID(),
                    "revoked-token",
                    testUserId,
                    true, // revoked
                    LocalDateTime.now(),
                    futureTime
            );

            when(repository.findByToken("revoked-token")).thenReturn(Optional.of(revokedToken));

            // Act & Assert
            assertThrows(InvalidRefreshToken.class, () -> {
                refreshTokenService.validateRefreshToken("revoked-token");
            }, "Should throw InvalidRefreshToken when token is revoked");
        }

        @Test
        @DisplayName("should throw exception for expired token")
        void shouldThrowExceptionForExpiredToken() {
            // Arrange
            LocalDateTime pastTime = LocalDateTime.now().minusDays(1);
            RefreshToken expiredToken = RefreshToken.restore(
                    UUID.randomUUID(),
                    "expired-token",
                    testUserId,
                    false,
                    LocalDateTime.now().minusDays(8),
                    pastTime // expired
            );

            when(repository.findByToken("expired-token")).thenReturn(Optional.of(expiredToken));

            // Act & Assert
            assertThrows(InvalidRefreshToken.class, () -> {
                refreshTokenService.validateRefreshToken("expired-token");
            }, "Should throw InvalidRefreshToken when token is expired");
        }

        @Test
        @DisplayName("should validate token for correct user")
        void shouldValidateTokenForCorrectUser() {
            // Arrange
            UUID userId = UUID.randomUUID();
            LocalDateTime futureTime = LocalDateTime.now().plusDays(7);
            RefreshToken validToken = RefreshToken.restore(
                    UUID.randomUUID(),
                    "valid-token",
                    userId,
                    false,
                    LocalDateTime.now(),
                    futureTime
            );

            when(repository.findByToken("valid-token")).thenReturn(Optional.of(validToken));

            // Act
            RefreshToken result = refreshTokenService.validateRefreshToken("valid-token");

            // Assert
            assertEquals(userId, result.getUserId());
        }
    }

    @Nested
    @DisplayName("revokeRefreshToken method tests")
    class RevokeRefreshTokenTests {

        @Test
        @DisplayName("should revoke a valid refresh token")
        void shouldRevokeValidRefreshToken() {
            // Arrange
            LocalDateTime futureTime = LocalDateTime.now().plusDays(7);
            RefreshToken token = RefreshToken.restore(
                    UUID.randomUUID(),
                    "token-to-revoke",
                    testUserId,
                    false,
                    LocalDateTime.now(),
                    futureTime
            );

            when(repository.findByToken("token-to-revoke")).thenReturn(Optional.of(token));

            // Act
            refreshTokenService.revokeRefreshToken("token-to-revoke");

            // Assert
            assertTrue(token.isRevoked(), "Token should be revoked");
            verify(repository, times(1)).save(token);
        }

        @Test
        @DisplayName("should throw exception when revoking non-existent token")
        void shouldThrowExceptionWhenRevokingNonExistentToken() {
            // Arrange
            when(repository.findByToken("non-existent")).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(InvalidRefreshToken.class, () -> {
                refreshTokenService.revokeRefreshToken("non-existent");
            }, "Should throw InvalidRefreshToken when token not found");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("should revoke token and persist to repository")
        void shouldRevokeTokenAndPersistToRepository() {
            // Arrange
            LocalDateTime futureTime = LocalDateTime.now().plusDays(7);
            RefreshToken token = RefreshToken.restore(
                    UUID.randomUUID(),
                    "token-to-persist",
                    testUserId,
                    false,
                    LocalDateTime.now(),
                    futureTime
            );

            when(repository.findByToken("token-to-persist")).thenReturn(Optional.of(token));

            // Act
            refreshTokenService.revokeRefreshToken("token-to-persist");

            // Assert
            ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
            verify(repository, times(1)).save(captor.capture());
            
            RefreshToken savedToken = captor.getValue();
            assertTrue(savedToken.isRevoked());
        }

        @Test
        @DisplayName("should revoke already revoked token without error")
        void shouldRevokeAlreadyRevokedTokenWithoutError() {
            // Arrange
            LocalDateTime futureTime = LocalDateTime.now().plusDays(7);
            RefreshToken token = RefreshToken.restore(
                    UUID.randomUUID(),
                    "already-revoked",
                    testUserId,
                    true, // already revoked
                    LocalDateTime.now(),
                    futureTime
            );

            when(repository.findByToken("already-revoked")).thenReturn(Optional.of(token));

            // Act & Assert
            assertDoesNotThrow(() -> {
                refreshTokenService.revokeRefreshToken("already-revoked");
            });

            verify(repository, times(1)).save(token);
        }
    }

    @Nested
    @DisplayName("deleteExpiredTokens method tests")
    class DeleteExpiredTokensTests {

        @Test
        @DisplayName("should call repository to delete expired tokens")
        void shouldCallRepositoryToDeleteExpiredTokens() {
            // Arrange
            doNothing().when(repository).deleteExpiredToken();

            // Act
            refreshTokenService.deleteExpiredTokens();

            // Assert
            verify(repository, times(1)).deleteExpiredToken();
        }

        @Test
        @DisplayName("should handle successful deletion")
        void shouldHandleSuccessfulDeletion() {
            // Arrange
            doNothing().when(repository).deleteExpiredToken();

            // Act & Assert
            assertDoesNotThrow(() -> {
                refreshTokenService.deleteExpiredTokens();
            });

            verify(repository, times(1)).deleteExpiredToken();
        }

        @Test
        @DisplayName("should be callable multiple times")
        void shouldBeCallableMultipleTimes() {
            // Arrange
            doNothing().when(repository).deleteExpiredToken();

            // Act
            refreshTokenService.deleteExpiredTokens();
            refreshTokenService.deleteExpiredTokens();
            refreshTokenService.deleteExpiredTokens();

            // Assert
            verify(repository, times(3)).deleteExpiredToken();
        }
    }

    @Nested
    @DisplayName("Integration tests")
    class IntegrationTests {

        @Test
        @DisplayName("should complete full refresh token lifecycle")
        void shouldCompleteFullRefreshTokenLifecycle() {
            // Arrange
            doNothing().when(repository).save(any(RefreshToken.class));

            // Act - Generate token
            RefreshToken generatedToken = refreshTokenService.generateRefreshToken(testUserId);

            // Assert - Token is generated
            assertNotNull(generatedToken);
            assertFalse(generatedToken.isRevoked());

            // Act - Prepare for validation
            LocalDateTime futureTime = LocalDateTime.now().plusDays(7);
            RefreshToken tokenForValidation = RefreshToken.restore(
                    generatedToken.getId(),
                    generatedToken.getToken(),
                    testUserId,
                    false,
                    generatedToken.getCreatedAt(),
                    futureTime
            );
            when(repository.findByToken(generatedToken.getToken()))
                    .thenReturn(Optional.of(tokenForValidation));

            // Assert - Token is valid
            RefreshToken validatedToken = refreshTokenService.validateRefreshToken(generatedToken.getToken());
            assertEquals(testUserId, validatedToken.getUserId());
            assertFalse(validatedToken.isRevoked());

            // Act - Revoke token
            when(repository.findByToken(generatedToken.getToken()))
                    .thenReturn(Optional.of(tokenForValidation));
            refreshTokenService.revokeRefreshToken(generatedToken.getToken());

            // Assert - Token is revoked
            assertTrue(tokenForValidation.isRevoked());
        }

        @Test
        @DisplayName("should handle multiple tokens for different users")
        void shouldHandleMultipleTokensForDifferentUsers() {
            // Arrange
            UUID userId1 = UUID.randomUUID();
            UUID userId2 = UUID.randomUUID();
            doNothing().when(repository).save(any(RefreshToken.class));

            // Act
            RefreshToken token1 = refreshTokenService.generateRefreshToken(userId1);
            RefreshToken token2 = refreshTokenService.generateRefreshToken(userId2);

            // Assert
            assertNotEquals(token1.getToken(), token2.getToken());
            assertEquals(userId1, token1.getUserId());
            assertEquals(userId2, token2.getUserId());

            verify(repository, times(2)).save(any(RefreshToken.class));
        }

        @Test
        @DisplayName("should revoke token without affecting other tokens")
        void shouldRevokeTokenWithoutAffectingOtherTokens() {
            // Arrange
            LocalDateTime futureTime = LocalDateTime.now().plusDays(7);
            RefreshToken token1 = RefreshToken.restore(
                    UUID.randomUUID(),
                    "token-1",
                    testUserId,
                    false,
                    LocalDateTime.now(),
                    futureTime
            );
            RefreshToken token2 = RefreshToken.restore(
                    UUID.randomUUID(),
                    "token-2",
                    testUserId,
                    false,
                    LocalDateTime.now(),
                    futureTime
            );

            when(repository.findByToken("token-1")).thenReturn(Optional.of(token1));

            // Act
            refreshTokenService.revokeRefreshToken("token-1");

            // Assert
            assertTrue(token1.isRevoked());
            assertFalse(token2.isRevoked());
        }

        @Test
        @DisplayName("should handle token generation and validation workflow")
        void shouldHandleTokenGenerationAndValidationWorkflow() {
            // Arrange
            doNothing().when(repository).save(any(RefreshToken.class));

            // Act
            RefreshToken generatedToken = refreshTokenService.generateRefreshToken(testUserId);
            String tokenString = generatedToken.getToken();

            LocalDateTime futureTime = LocalDateTime.now().plusDays(7);
            RefreshToken storedToken = RefreshToken.restore(
                    UUID.randomUUID(),
                    tokenString,
                    testUserId,
                    false,
                    generatedToken.getCreatedAt(),
                    futureTime
            );
            when(repository.findByToken(tokenString)).thenReturn(Optional.of(storedToken));

            // Assert
            RefreshToken validatedToken = refreshTokenService.validateRefreshToken(tokenString);
            assertEquals(tokenString, validatedToken.getToken());
            assertEquals(testUserId, validatedToken.getUserId());
        }
    }
}
