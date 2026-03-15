package com.example.prueba.infrastructure.entrypoint.exception;

import com.example.prueba.infrastructure.entrypoint.dto.response.ApiResponse;
import com.example.prueba.infrastructure.entrypoint.dto.response.InsufficientBalanceData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

	private GlobalExceptionHandler handler;

	@BeforeEach
	void setUp() {
		handler = new GlobalExceptionHandler();
	}

	@Test
	@DisplayName("insufficientBalance returns 422 and InsufficientBalanceData")
	void insufficientBalance() {
		InsufficientBalanceException ex = new InsufficientBalanceException("FondoX", new BigDecimal("10000"));
		ResponseEntity<ApiResponse<InsufficientBalanceData>> result = handler.insufficientBalance(ex);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
		assertThat(result.getBody()).isNotNull();
		assertThat(result.getBody().isSuccess()).isFalse();
		assertThat(result.getBody().getMessage()).contains("No tiene saldo disponible");
		assertThat(result.getBody().getData()).isNotNull();
		assertThat(result.getBody().getData().getAvailableBalance()).isEqualByComparingTo(new BigDecimal("10000"));
	}

	@Test
	@DisplayName("resourceNotFound returns 404 and error message")
	void resourceNotFound() {
		ResourceNotFoundException ex = new ResourceNotFoundException("Recurso no encontrado");
		ResponseEntity<ApiResponse<Void>> result = handler.resourceNotFound(ex);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(result.getBody()).isNotNull();
		assertThat(result.getBody().isSuccess()).isFalse();
		assertThat(result.getBody().getMessage()).isEqualTo("Recurso no encontrado");
	}

	@Test
	@DisplayName("invalidArgument returns 400 and error message")
	void invalidArgument() {
		IllegalArgumentException ex = new IllegalArgumentException("Monto inválido");
		ResponseEntity<ApiResponse<Void>> result = handler.invalidArgument(ex);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(result.getBody()).isNotNull();
		assertThat(result.getBody().getMessage()).isEqualTo("Monto inválido");
	}
}
