package com.SecureBankingApi.application.usecases;

import com.SecureBankingApi.IntegrationTestBase;
import com.SecureBankingApi.application.usecases.createAccount.AccountResponse;
import com.SecureBankingApi.application.usecases.loginUser.LoginUserResponse;
import com.SecureBankingApi.application.usecases.registerUser.RegisterUserResponse;
import com.SecureBankingApi.domain.account.AccountType;
import com.SecureBankingApi.infrastructure.api.webDtos.CreateAccountWebRequest;
import com.SecureBankingApi.infrastructure.api.webDtos.LoginWebRequest;
import com.SecureBankingApi.infrastructure.api.webDtos.RegisterWebRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountIntegrationTest extends IntegrationTestBase {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanUp() {
        initWebTestClient();
        jdbcTemplate.execute("TRUNCATE TABLE transactions, accounts, refresh_tokens, users RESTART IDENTITY CASCADE");
    }

    @Test
    void shouldCreateAccountSuccessfully() {

        String token = registerAndLogin("joao@email.com", "12345678901", "senha12345");

        CreateAccountWebRequest request = new CreateAccountWebRequest();
        request.setType(AccountType.SAVINGS);


        webTestClient.post()
                        .uri("/api/accounts/create")
                                .headers(h -> h.setBearerAuth(token))
                                        .contentType(MediaType.APPLICATION_JSON)
                                                .bodyValue(request)
                                                        .exchange()
                                                                .expectStatus().isCreated()
                        .expectBody(AccountResponse.class)
                                .value(body -> {
                                    assertNotNull(body);
                                    assertNull(body.getId());
                                    assertEquals(AccountType.CHECKING, body.getType());
                                    assertEquals(BigDecimal.ZERO, body.getBalance().getValue());
                                });
    }

    @Test
    void shouldBlockAnAccount() {
        String token = registerAndLogin("joao@email.com", "12345678901", "senha12345");
    }

    private String registerAndLogin(String email, String cpf, String password) {
        RegisterWebRequest register = new RegisterWebRequest();
        register.setEmail(email);
        register.setFullName("Joao Silva");
        register.setCpf(cpf);
        register.setPassword(password);

        webTestClient.post()
                .uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(register)
                .exchange()
                .expectStatus().isCreated();

        LoginWebRequest login = new LoginWebRequest();
        login.setEmail(email);
        login.setPassword(password);
        LoginUserResponse loginResponse = webTestClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(login)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginUserResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(loginResponse);
        assertNotNull(loginResponse.getAccessToken());
        return loginResponse.getAccessToken();
    }
}
