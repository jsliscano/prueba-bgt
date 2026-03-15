package com.example.prueba.infrastructure.entrypoint.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransactionHistoryRequest {
	@NotNull(message = "El ID del cliente es obligatorio.")
	@JsonProperty("clienteId")
	private String clientId;
}
