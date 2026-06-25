package com.SecureBankingApi.application.usecases;

import com.SecureBankingApi.application.usecases.createAccount.AccountResponse;
import com.SecureBankingApi.application.usecases.loginUser.LoginUserResponse;
import com.SecureBankingApi.domain.account.AccountType;
import com.SecureBankingApi.infrastructure.api.webDtos.CreateAccountWebRequest;
import com.SecureBankingApi.infrastructure.api.webDtos.LoginWebRequest;
import com.SecureBankingApi.infrastructure.api.webDtos.RegisterWebRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
class AccountIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @LocalServerPort
    private int port;

    private RestClient restClient;


    @BeforeEach
    void setup() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
        jdbcTemplate.execute("TRUNCATE TABLE transactions, accounts, refresh_tokens, users RESTART IDENTITY CASCADE");
    }

    @Test
    void shouldCreateAccountSuccessfully() {

        String token = registerAndLogin("joao@email.com", "12345678901", "senha12345");

        CreateAccountWebRequest request = new CreateAccountWebRequest();
        request.setType(AccountType.SAVINGS);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateAccountWebRequest> entity =
                new HttpEntity<>(request, headers);

        AccountResponse response = restClient.post()
                .uri("/api/accounts/create")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(AccountResponse.class);

    }

    @Test
    void shouldBlockAnAccount() {
        String token = registerAndLogin("joao@email.com", "12345678901", "senha12345");
    }

    private String registerAndLogin(
            String email,
            String cpf,
            String password) {

        RegisterWebRequest register = new RegisterWebRequest();
        register.setEmail(email);
        register.setFullName("Joao Silva");
        register.setCpf(cpf);
        register.setPassword(password);

        restClient.post()
                .uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(register)
                .retrieve()
                .toBodilessEntity();

        LoginWebRequest login = new LoginWebRequest();
        login.setEmail(email);
        login.setPassword(password);

        LoginUserResponse loginResponse =
                restClient.post()
                        .uri("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(login)
                        .retrieve()
                        .body(LoginUserResponse.class);

        assertNotNull(loginResponse);
        assertNotNull(loginResponse.getAccessToken());

        return loginResponse.getAccessToken();
    }
}
