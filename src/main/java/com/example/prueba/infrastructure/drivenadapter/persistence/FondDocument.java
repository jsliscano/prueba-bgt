package com.example.prueba.infrastructure.drivenadapter.persistence;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.prueba.domain.model.enums.FundCategory;
import com.example.prueba.domain.model.Fund;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fondos")
public class FondDocument {

	@Id
	private Long id;
	private String nombre;
	private BigDecimal montoMinimo;
	private String categoria;

	public static FondDocument from(Fund f) {
		return FondDocument.builder()
				.id(f.getId())
				.nombre(f.getName())
				.montoMinimo(f.getMinimumAmount())
				.categoria(f.getCategory().name())
				.build();
	}

	public Fund toDomain() {
		return Fund.builder()
				.id(id)
				.name(nombre)
				.minimumAmount(montoMinimo)
				.category(FundCategory.valueOf(categoria))
				.build();
	}
}
