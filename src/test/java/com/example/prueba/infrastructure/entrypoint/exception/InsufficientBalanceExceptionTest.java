package com.example.prueba.infrastructure.entrypoint.exception;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InsufficientBalanceException")
class InsufficientBalanceExceptionTest {

	@Test
	@DisplayName("message contains fund name")
	void messageContainsFundName() {
		InsufficientBalanceException ex = new InsufficientBalanceException("FPV_BTG", new BigDecimal("50000"));
		assertThat(ex.getMessage()).contains("FPV_BTG");
		assertThat(ex.getMessage()).contains("No tiene saldo disponible");
	}

	@Test
	@DisplayName("getAvailableBalance returns value passed in constructor")
	void getAvailableBalance() {
		BigDecimal balance = new BigDecimal("12345.67");
		InsufficientBalanceException ex = new InsufficientBalanceException("F1", balance);
		assertThat(ex.getAvailableBalance()).isEqualTo(balance);
	}
}
