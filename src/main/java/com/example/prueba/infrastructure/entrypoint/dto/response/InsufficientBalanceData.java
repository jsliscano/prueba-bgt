package com.example.prueba.infrastructure.entrypoint.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsufficientBalanceData {
	private BigDecimal availableBalance;
}
