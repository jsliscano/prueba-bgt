package com.example.prueba.infrastructure.entrypoint.dto.request;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RechargeBalanceRequest {
	@NotNull(message = "El ID del cliente es obligatorio.")
	@JsonProperty("clienteId")
	private String clientId;
	@NotNull(message = "El monto es obligatorio.")
	@DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero.")
	@JsonProperty("monto")
	private BigDecimal amount;
}

