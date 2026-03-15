package com.example.prueba.domain.usecase;

import com.example.prueba.infrastructure.entrypoint.exception.ResourceNotFoundException;
import com.example.prueba.domain.model.Transaction;
import com.example.prueba.domain.port.ClientRepository;
import com.example.prueba.domain.port.TransactionRepository;
import java.util.List;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class TransactionHistoryUseCase {

	private final ClientRepository clientRepository;
	private final TransactionRepository transactionRepository;

	public Mono<List<Transaction>> execute(String clientId) {
		return clientRepository.searchById(clientId)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("Cliente no encontrado: " + clientId)))
				.flatMap(c -> transactionRepository.ListClient(clientId).collectList());
	}
}
