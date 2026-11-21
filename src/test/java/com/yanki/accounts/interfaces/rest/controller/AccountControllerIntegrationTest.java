package com.yanki.accounts.interfaces.rest.controller;

import com.yanki.accounts.domain.service.AccountService;
import com.yanki.accounts.domain.service.AccountResult;
import com.yanki.accounts.interfaces.rest.dto.AccountRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static io.reactivex.rxjava3.core.Single.just;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class AccountControllerIntegrationTest {

    @MockBean
    private AccountService accountService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void createAccount_withInvalidInput_shouldReturnBadRequest() {
        AccountRequest request = AccountRequest.builder()
                .customerId("") // Invalid
                .accountType("SAVINGS")
                .currency("PEN")
                .initialAmount(new BigDecimal("1000"))
                .holders(Collections.singletonList(
                        AccountRequest.AccountHolderRequest.builder().holderId("H001").build()
                ))
                .build();

        webTestClient.post().uri("/api/accounts")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
                .jsonPath("$.message").isEqualTo("Datos de entrada inválidos");
    }

    @Nested
    @DisplayName("Deserialización JSON y validación de createAccount")
    class CreateAccountJsonDeserializationTests {

        @Test
        @DisplayName("Debe deserializar correctamente el JSON de Postman con accountType y retornar 200")
        void createAccount_withPostmanJson_shouldDeserializeAndSucceed() {
            // Arrange: JSON exacto que envía Postman
            String jsonPayload = "{"
                    + "\"customerId\": \"6af9d9506d578f772b090b98\","
                    + "\"accountType\": \"SAVINGS\","
                    + "\"currency\": \"PEN\","
                    + "\"initialAmount\": 1000.00,"
                    + "\"channel\": \"WEB\""
                    + "}";

            AccountResult result = AccountResult.builder()
                    .accountId("ACC123")
                    .accountType("SAVINGS")
                    .customerId("6af9d9506d578f772b090b98")
                    .currency("PEN")
                    .balance(new BigDecimal("1000.00"))
                    .build();

            when(accountService.openAccount(any())).thenReturn(just(result));

            // Act & Assert
            webTestClient.post()
                    .uri("/api/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jsonPayload)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.accountId").isEqualTo("ACC123")
                    .jsonPath("$.accountType").isEqualTo("SAVINGS")
                    .jsonPath("$.customerId").isEqualTo("6af9d9506d578f772b090b98");
        }

        @Test
        @DisplayName("Debe fallar con 400 si el JSON no tiene accountType")
        void createAccount_withoutAccountType_shouldReturn400() {
            String jsonPayload = "{"
                    + "\"customerId\": \"6af9d9506d578f772b090b98\","
                    + "\"currency\": \"PEN\","
                    + "\"initialAmount\": 1000.00,"
                    + "\"channel\": \"WEB\""
                    + "}";

            webTestClient.post()
                    .uri("/api/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jsonPayload)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
                    .jsonPath("$.details.accountType").isNotEmpty();
        }

        @Test
        @DisplayName("Debe fallar con 400 si el JSON no tiene customerId")
        void createAccount_withoutCustomerId_shouldReturn400() {
            String jsonPayload = "{"
                    + "\"accountType\": \"SAVINGS\","
                    + "\"currency\": \"PEN\","
                    + "\"initialAmount\": 1000.00,"
                    + "\"channel\": \"WEB\""
                    + "}";

            webTestClient.post()
                    .uri("/api/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jsonPayload)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
                    .jsonPath("$.details.customerId").isNotEmpty();
        }

        @Test
        @DisplayName("Debe fallar con 400 si el JSON no tiene currency")
        void createAccount_withoutCurrency_shouldReturn400() {
            String jsonPayload = "{"
                    + "\"customerId\": \"6af9d9506d578f772b090b98\","
                    + "\"accountType\": \"SAVINGS\","
                    + "\"initialAmount\": 1000.00,"
                    + "\"channel\": \"WEB\""
                    + "}";

            webTestClient.post()
                    .uri("/api/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jsonPayload)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
                    .jsonPath("$.details.currency").isNotEmpty();
        }

        @Test
        @DisplayName("Debe fallar con 400 si el JSON no tiene initialAmount")
        void createAccount_withoutInitialAmount_shouldReturn400() {
            String jsonPayload = "{"
                    + "\"customerId\": \"6af9d9506d578f772b090b98\","
                    + "\"accountType\": \"SAVINGS\","
                    + "\"currency\": \"PEN\","
                    + "\"channel\": \"WEB\""
                    + "}";

            webTestClient.post()
                    .uri("/api/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jsonPayload)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
                    .jsonPath("$.details.initialAmount").isNotEmpty();
        }

        @Test
        @DisplayName("Debe fallar con 400 si todos los campos obligatorios faltan")
        void createAccount_withAllFieldsMissing_shouldReturn400() {
            String jsonPayload = "{}";

            webTestClient.post()
                    .uri("/api/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jsonPayload)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
                    .jsonPath("$.details.customerId").isNotEmpty()
                    .jsonPath("$.details.accountType").isNotEmpty()
                    .jsonPath("$.details.currency").isNotEmpty()
                    .jsonPath("$.details.initialAmount").isNotEmpty();
        }
    }
}
