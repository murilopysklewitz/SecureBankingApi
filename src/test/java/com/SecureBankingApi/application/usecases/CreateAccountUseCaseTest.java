package com.SecureBankingApi.application.usecases;

import com.SecureBankingApi.IntegrationTestBase;
import com.SecureBankingApi.SecureBankingApiApplication;
import com.SecureBankingApi.application.exceptions.BusinessException;
import com.SecureBankingApi.application.usecases.createAccount.AccountResponse;
import com.SecureBankingApi.application.usecases.createAccount.CreateAccountRequest;
import com.SecureBankingApi.application.usecases.createAccount.CreateAccountUseCase;
import com.SecureBankingApi.application.usecases.loginUser.LoginUserResponse;
import com.SecureBankingApi.application.usecases.registerUser.RegisterUserResponse;
import com.SecureBankingApi.domain.account.*;
import com.SecureBankingApi.infrastructure.api.webDtos.CreateAccountWebRequest;
import com.SecureBankingApi.infrastructure.api.webDtos.LoginWebRequest;
import com.SecureBankingApi.infrastructure.api.webDtos.RegisterWebRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.C;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateAccountUseCaseTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private CreateAccountUseCase createAccountUseCase;

    private UUID userId;
    private AccountType accountType;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        accountType = AccountType.CHECKING;
    }

    @Test
    public void shouldCreateAccountSuccessfully() {
        // Given
        CreateAccountRequest request = new CreateAccountRequest(userId, accountType);
        when(accountRepository.existsByUserIdAndType(userId, accountType)).thenReturn(false);

        // When
        var response = createAccountUseCase.execute(request);

        // Then
        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals(accountType, response.getType());
        assertEquals("001", response.getAgency());
        assertEquals(AccountStatus.ACTIVE, response.getStatus());
        assertEquals(Money.zero(), response.getBalance());
        assertNotNull(response.getAccountNumber());
        assertNull(response.getId());
        assertNotNull(response.getCreatedAt());

        verify(accountRepository).existsByUserIdAndType(userId, accountType);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    public void shouldThrowBusinessExceptionWhenAccountOfSameTypeAlreadyExists() {
        // Given
        CreateAccountRequest request = new CreateAccountRequest(userId, accountType);
        when(accountRepository.existsByUserIdAndType(userId, accountType)).thenReturn(true);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> createAccountUseCase.execute(request));
        assertEquals("Already have an account with same time for this user", exception.getMessage());

        verify(accountRepository).existsByUserIdAndType(userId, accountType);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void shouldGenerateUniqueAccountNumber() {
        // Given
        CreateAccountRequest request1 = new CreateAccountRequest(userId, accountType);
        CreateAccountRequest request2 = new CreateAccountRequest(UUID.randomUUID(), accountType);
        when(accountRepository.existsByUserIdAndType(any(UUID.class), eq(accountType))).thenReturn(false);

        // When
        var response1 = createAccountUseCase.execute(request1);
        var response2 = createAccountUseCase.execute(request2);

        // Then
        assertNotEquals(response1.getAccountNumber(), response2.getAccountNumber());
    }
}
    class AccountIntegrationTest extends IntegrationTestBase {

        @Autowired
        private JdbcTemplate jdbcTemplate;

        @BeforeEach
        void cleanUp() {
            jdbcTemplate.execute("TRUNCATE TABLE transactions, accounts, refresh_tokens, users RESTART IDENTITY CASCADE");
        }

        @Test
        void shouldCreateAccountSuccessfully() {
            String token = registerAndLogin("joao@email.com", "12345678901", "senha12345");

            CreateAccountWebRequest request = new CreateAccountWebRequest();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<CreateAccountWebRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<AccountResponse> response = restTemplate.exchange(
                    getBaseUrl("/api/accounts/create"),
                    HttpMethod.POST,
                    entity,
                    AccountResponse.class
            );

            assertEquals(HttpStatus.CREATED, response.getStatusCode());

            var body = response.getBody();
            assertNotNull(body);
            assertNotNull(body.getId());
            assertEquals(AccountType.CHECKING, body.getType());
            assertEquals(BigDecimal.ZERO, body.getBalance().getValue());
        }

        private String registerAndLogin(String email, String cpf, String password) {
            RegisterWebRequest register = new RegisterWebRequest();
            register.setEmail(email);
            register.setFullName("Joao Silva");
            register.setCpf(cpf);
            register.setPassword(password);
            restTemplate.postForEntity(getBaseUrl("/api/auth/register"), register, RegisterUserResponse.class);

            LoginWebRequest login = new LoginWebRequest();
            login.setEmail(email);
            login.setPassword(password);
            ResponseEntity<LoginUserResponse> loginResponse = restTemplate.postForEntity(
                    getBaseUrl("/api/auth/login"), login, LoginUserResponse.class
            );
            assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
            assertNotNull(loginResponse.getBody());
            assertNotNull(loginResponse.getBody().getAccessToken());
            return loginResponse.getBody().getAccessToken();
        }
    }
