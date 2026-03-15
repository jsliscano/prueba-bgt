package com.example.prueba.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

import com.example.prueba.domain.model.enums.TransactionType;
import lombok.Builder;
import lombok.Value;


@Value
@Builder
public class Transaction {
	String id;
	String clientId;
	Long fondId;
	TransactionType type;
	BigDecimal amount;
	Instant date;
}
