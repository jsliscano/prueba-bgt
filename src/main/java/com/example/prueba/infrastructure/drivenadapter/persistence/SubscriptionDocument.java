package com.example.prueba.infrastructure.drivenadapter.persistence;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.prueba.domain.model.Subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "suscripciones")
public class SubscriptionDocument {

	@Id
	private String id;
	private String clienteId;
	private Long fondoId;
	private BigDecimal montoVinculado;
	private boolean activa;

	public static SubscriptionDocument from(Subscription s) {
		return SubscriptionDocument.builder()
				.id(s.getId())
				.clienteId(s.getClientId())
				.fondoId(s.getFondId())
				.montoVinculado(s.getAmountInvolved())
				.activa(s.isActive())
				.build();
	}

	public Subscription toDomain() {
		return Subscription.builder()
				.id(id)
				.clientId(clienteId)
				.fondId(fondoId)
				.amountInvolved(montoVinculado)
				.active(activa)
				.build();
	}
}
