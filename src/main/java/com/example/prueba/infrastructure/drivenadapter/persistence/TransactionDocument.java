package com.example.prueba.infrastructure.drivenadapter.persistence;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.prueba.domain.model.Transaction;
import com.example.prueba.domain.model.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transacciones")
public class TransactionDocument {

	@Id
	private String id;
	private String clienteId;
	private Long fondoId;
	private String tipo;
	private BigDecimal monto;
	private Instant fecha;

	public static TransactionDocument from(Transaction t) {
		return TransactionDocument.builder()
				.id(t.getId())
				.clienteId(t.getClientId())
				.fondoId(t.getFondId())
				.tipo(t.getType().name())
				.monto(t.getAmount())
				.fecha(t.getDate())
				.build();
	}

	public Transaction toDomain() {
		return Transaction.builder()
				.id(id)
				.clientId(clienteId)
				.fondId(fondoId)
				.type(TransactionType.valueOf(tipo))
				.amount(monto)
				.date(fecha)
				.build();
	}
}
