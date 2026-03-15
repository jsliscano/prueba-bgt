package com.example.prueba.domain.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscribeResult {
	private Transaction transaction;
	private BigDecimal remainingBalance;
}
