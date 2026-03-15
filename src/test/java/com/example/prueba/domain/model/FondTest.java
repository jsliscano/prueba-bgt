package com.example.prueba.domain.model;

import java.math.BigDecimal;

import com.example.prueba.domain.model.enums.FundCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Fondo")
class FondTest {

	@Test
	@DisplayName("meets minimum amount when monto >= minimumAmount")
	void meetsMinimumAmount() {
		Fund f = Fund.builder()
				.id(1L)
				.name("FDO")
				.minimumAmount(new BigDecimal("100000"))
				.category(FundCategory.FPV)
				.build();
		assertThat(f.meetsMinimumAmount(new BigDecimal("100000"))).isTrue();
		assertThat(f.meetsMinimumAmount(new BigDecimal("150000"))).isTrue();
		assertThat(f.meetsMinimumAmount(new BigDecimal("99999"))).isFalse();
	}
}
