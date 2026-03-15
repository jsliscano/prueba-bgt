package com.example.prueba.infrastructure.entrypoint.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CancelRequest {
	@NotNull(message = "El ID del cliente es obligatorio.")
	@JsonProperty("clienteId")
	private String clientId;
	@NotNull(message = "El ID del fondo es obligatorio.")
	@JsonProperty("fondoId")
	private Long fondId;
}
