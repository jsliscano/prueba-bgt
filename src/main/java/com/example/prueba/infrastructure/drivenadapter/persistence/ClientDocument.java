package com.example.prueba.infrastructure.drivenadapter.persistence;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.prueba.domain.model.Client;
import com.example.prueba.domain.model.enums.NotificationPreference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "clientes")
public class ClientDocument {

	@Id
	private String id;
	private String nombre;
	private BigDecimal saldo;
	private String preferenciaNotificacion;
	private String email;
	private String telefono;

	public static ClientDocument from(Client c) {
		return ClientDocument.builder()
				.id(c.getId())
				.nombre(c.getName())
				.saldo(c.getAmount())
				.preferenciaNotificacion(c.getNotificationPreference().name())
				.email(c.getEmail())
				.telefono(c.getPhone())
				.build();
	}

	public Client toDomain() {
		return Client.builder()
				.id(id)
				.name(nombre)
				.amount(saldo)
				.notificationPreference(NotificationPreference.valueOf(preferenciaNotificacion))
				.email(email)
				.phone(telefono)
				.build();
	}
}
