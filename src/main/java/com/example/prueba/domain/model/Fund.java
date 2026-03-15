package com.example.prueba.domain.model;

import java.math.BigDecimal;

import com.example.prueba.domain.model.enums.FundCategory;
import lombok.Builder;
import lombok.Value;


@Value
@Builder
public class Fund {
	Long id;
	String name;
	BigDecimal minimumAmount;
	FundCategory category;

	public boolean meetsMinimumAmount(BigDecimal amount) {
		return amount != null && minimumAmount != null && amount.compareTo(minimumAmount) >= 0;
	}
}
