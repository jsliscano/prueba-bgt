package com.example.prueba.infrastructure.entrypoint.dto.request;

import com.example.prueba.domain.model.enums.NotificationPreference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateClientRequest {
	private String id;
	@NotBlank(message = "El nombre del cliente es obligatorio.")
	@JsonProperty("nombre")
	private String name;
	@JsonProperty("preferenciaNotificacion")
	private NotificationPreference notificationPreference = NotificationPreference.EMAIL;
	@Email(message = "El correo debe tener un formato válido")
	private String email;
	@Pattern(regexp = "^$|^\\+?[0-9]{7,15}$", message = "El celular debe contener solo números")
	@JsonProperty("telefono")
	private String phone;
	
}
