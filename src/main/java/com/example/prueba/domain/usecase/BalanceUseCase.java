package com.example.prueba.domain.usecase;

import com.example.prueba.infrastructure.entrypoint.exception.ResourceNotFoundException;
import com.example.prueba.domain.model.Client;
import com.example.prueba.domain.port.ClientRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

@RequiredArgsConstructor
public class BalanceUseCase {

	private final ClientRepository clientRepository;

	public Mono<Client> execute(String clientId, BigDecimal amount) {
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			return Mono.error(new IllegalArgumentException("El monto a recargar debe ser mayor que cero."));
		}
		return clientRepository.searchById(clientId)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("Cliente no encontrado: " + clientId)))
				.flatMap(client -> {
					BigDecimal newBalance = client.getAmount().add(amount);
					Client  updatedclient = Client.builder()
							.id(client.getId())
							.name(client.getName())
							.amount(newBalance)
							.notificationPreference(client.getNotificationPreference())
							.email(client.getEmail())
							.phone(client.getPhone())
							.build();
					return clientRepository.saveClient( updatedclient);
				});
	}
}
