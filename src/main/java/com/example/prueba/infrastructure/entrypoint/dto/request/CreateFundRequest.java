package com.example.prueba.infrastructure.entrypoint.dto.request;

import java.math.BigDecimal;

import com.example.prueba.domain.model.enums.FundCategory;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateFundRequest {
	@NotNull
	private Long id;
	@NotNull
	@JsonProperty("nombre")
	private String name;
	@NotNull
	@DecimalMin("0")
	@JsonProperty("montoMinimo")
	private BigDecimal minimumAmount;
	@NotNull
	@JsonProperty("categoria")
	private FundCategory category;
}
