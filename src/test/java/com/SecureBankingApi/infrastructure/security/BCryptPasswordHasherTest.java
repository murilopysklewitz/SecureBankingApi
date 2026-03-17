package com.SecureBankingApi.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BCryptPasswordHasher Tests")
public class BCryptPasswordHasherTest {

    private BCryptPasswordHasher passwordHasher;

    @BeforeEach
    void setUp() {
        passwordHasher = new BCryptPasswordHasher();
    }

    @Nested
    @DisplayName("hash method tests")
    class HashMethodTests {

        @Test
        @DisplayName("should hash a password successfully")
        void shouldHashPasswordSuccessfully() {
            // Arrange
            String rawPassword = "SecurePassword123!";

            // Act
            String hashedPassword = passwordHasher.hash(rawPassword);

            // Assert
            assertNotNull(hashedPassword);
            assertNotEquals(rawPassword, hashedPassword);
            assertTrue(hashedPassword.length() > 0);
        }

        @Test
        @DisplayName("should generate different hashes for the same password")
        void shouldGenerateDifferentHashesForSamePassword() {
            // Arrange
            String rawPassword = "SecurePassword123!";

            // Act
            String hash1 = passwordHasher.hash(rawPassword);
            String hash2 = passwordHasher.hash(rawPassword);

            // Assert
            assertNotEquals(hash1, hash2, "BCrypt should generate different hashes due to salt");
        }

        @Test
        @DisplayName("should hash empty string")
        void shouldHashEmptyString() {
            // Arrange
            String emptyPassword = "";

            // Act
            String hashedPassword = passwordHasher.hash(emptyPassword);

            // Assert
            assertNotNull(hashedPassword);
            assertNotEquals(emptyPassword, hashedPassword);
        }

        @Test
        @DisplayName("should hash password up to 72 bytes limit")
        void shouldHashPasswordUpTo72BytesLimit() {
            // Arrange - BCrypt has a 72 byte password limit
            String longPassword = "a".repeat(72);

            // Act
            String hashedPassword = passwordHasher.hash(longPassword);

            // Assert
            assertNotNull(hashedPassword);
            assertTrue(hashedPassword.length() > 0);
        }

        @Test
        @DisplayName("should throw exception for password exceeding 72 bytes")
        void shouldThrowExceptionForPasswordExceeding72Bytes() {
            // Arrange
            String tooLongPassword = "a".repeat(73);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                passwordHasher.hash(tooLongPassword);
            }, "BCrypt should reject passwords longer than 72 bytes");
        }

        @Test
        @DisplayName("should hash password with special characters")
        void shouldHashPasswordWithSpecialCharacters() {
            // Arrange
            String specialPassword = "P@$$w0rd!#%&*()_+-=[]{}|;:',.<>?/~`";

            // Act
            String hashedPassword = passwordHasher.hash(specialPassword);

            // Assert
            assertNotNull(hashedPassword);
            assertNotEquals(specialPassword, hashedPassword);
        }

        @Test
        @DisplayName("should hash password with unicode characters")
        void shouldHashPasswordWithUnicodeCharacters() {
            // Arrange
            String unicodePassword = "Senhaçü™€";

            // Act
            String hashedPassword = passwordHasher.hash(unicodePassword);

            // Assert
            assertNotNull(hashedPassword);
            assertNotEquals(unicodePassword, hashedPassword);
        }
    }

    @Nested
    @DisplayName("compare method tests")
    class CompareMethodTests {

        @Test
        @DisplayName("should return true when comparing raw password with its hash")
        void shouldReturnTrueForMatchingPassword() {
            // Arrange
            String rawPassword = "SecurePassword123!";
            String hashedPassword = passwordHasher.hash(rawPassword);

            // Act
            boolean matches = passwordHasher.compare(rawPassword, hashedPassword);

            // Assert
            assertTrue(matches);
        }

        @Test
        @DisplayName("should return false when comparing different passwords")
        void shouldReturnFalseForDifferentPasswords() {
            // Arrange
            String rawPassword = "SecurePassword123!";
            String wrongPassword = "WrongPassword456@";
            String hashedPassword = passwordHasher.hash(rawPassword);

            // Act
            boolean matches = passwordHasher.compare(wrongPassword, hashedPassword);

            // Assert
            assertFalse(matches);
        }

        @Test
        @DisplayName("should return false when comparing empty string with hashed password")
        void shouldReturnFalseForEmptyStringComparison() {
            // Arrange
            String rawPassword = "SecurePassword123!";
            String hashedPassword = passwordHasher.hash(rawPassword);

            // Act
            boolean matches = passwordHasher.compare("", hashedPassword);

            // Assert
            assertFalse(matches);
        }

        @Test
        @DisplayName("should be case sensitive")
        void shouldBeCaseSensitive() {
            // Arrange
            String rawPassword = "SecurePassword123!";
            String wrongCasePassword = "securepassword123!";
            String hashedPassword = passwordHasher.hash(rawPassword);

            // Act
            boolean matches = passwordHasher.compare(wrongCasePassword, hashedPassword);

            // Assert
            assertFalse(matches);
        }

        @Test
        @DisplayName("should handle passwords at 72 byte limit correctly")
        void shouldHandle72ByteLimitPasswordsCorrectly() {
            // Arrange - BCrypt maximum is 72 bytes
            String limitPassword = "x".repeat(72);
            String hashedPassword = passwordHasher.hash(limitPassword);

            // Act
            boolean matches = passwordHasher.compare(limitPassword, hashedPassword);

            // Assert
            assertTrue(matches);
        }

        @Test
        @DisplayName("should handle special characters in comparison")
        void shouldHandleSpecialCharactersInComparison() {
            // Arrange
            String specialPassword = "P@$$w0rd!#%&*()_+-=[]{}|;:',.<>?/~`";
            String hashedPassword = passwordHasher.hash(specialPassword);

            // Act
            boolean matches = passwordHasher.compare(specialPassword, hashedPassword);

            // Assert
            assertTrue(matches);
        }

        @Test
        @DisplayName("should handle unicode characters in comparison")
        void shouldHandleUnicodeCharactersInComparison() {
            // Arrange
            String unicodePassword = "Senhaçü™€";
            String hashedPassword = passwordHasher.hash(unicodePassword);

            // Act
            boolean matches = passwordHasher.compare(unicodePassword, hashedPassword);

            // Assert
            assertTrue(matches);
        }

        @Test
        @DisplayName("should throw exception when hash is malformed")
        void shouldThrowExceptionForMalformedHash() {
            // Arrange
            String rawPassword = "SecurePassword123!";
            String malformedHash = "notavalidhash";

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                passwordHasher.compare(rawPassword, malformedHash);
            }, "BCrypt should throw exception for invalid salt");
        }
    }

    @Nested
    @DisplayName("integration tests")
    class IntegrationTests {

        @Test
        @DisplayName("should handle complete password workflow")
        void shouldHandleCompletePasswordWorkflow() {
            // Arrange
            String userPassword = "MySecurePassword123!";

            // Act - Hash the password during registration
            String hashedPasswordInDb = passwordHasher.hash(userPassword);

            // Assert - Password should be hashed and stored
            assertNotNull(hashedPasswordInDb);
            assertNotEquals(userPassword, hashedPasswordInDb);

            // Act - Verify password during login
            boolean isLoginValid = passwordHasher.compare(userPassword, hashedPasswordInDb);

            // Assert - Password should match
            assertTrue(isLoginValid);

            // Act - Try to login with wrong password
            String wrongPassword = "WrongPassword456@";
            boolean isWrongPasswordValid = passwordHasher.compare(wrongPassword, hashedPasswordInDb);

            // Assert - Wrong password should not match
            assertFalse(isWrongPasswordValid);
        }

        @Test
        @DisplayName("should be consistent across multiple operations")
        void shouldBeConsistentAcrossMultipleOperations() {
            // Arrange
            String password1 = "Password1";
            String password2 = "Password2";

            // Act & Assert - Hash and compare for password 1
            String hash1 = passwordHasher.hash(password1);
            assertTrue(passwordHasher.compare(password1, hash1));
            assertFalse(passwordHasher.compare(password2, hash1));

            // Act & Assert - Hash and compare for password 2
            String hash2 = passwordHasher.hash(password2);
            assertTrue(passwordHasher.compare(password2, hash2));
            assertFalse(passwordHasher.compare(password1, hash2));
        }
    }
}
