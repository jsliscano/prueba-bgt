package com.example.prueba.infrastructure.entrypoint.exception;

import lombok.Getter;
import java.math.BigDecimal;


@Getter
public class InsufficientBalanceException extends RuntimeException {

	private final BigDecimal availableBalance;

	public InsufficientBalanceException(String fundName, BigDecimal availableBalance) {
		super("No tiene saldo disponible para vincularse al fondo " + fundName);
		this.availableBalance = availableBalance;
	}

}
