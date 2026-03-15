package com.example.prueba.infrastructure.drivenadapter.persistence;

import org.springframework.stereotype.Component;

import com.example.prueba.domain.model.Transaction;
import com.example.prueba.domain.port.TransactionRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class MongodbTransactionRepository implements TransactionRepository {

	private final SpringDataTransactionRepository repo;

	public MongodbTransactionRepository(SpringDataTransactionRepository repo) {
		this.repo = repo;
	}

	@Override
	public Mono<Transaction> saveTransaction(Transaction transaction) {
		return Mono.just(TransactionDocument.from(transaction))
				.flatMap(repo::save)
				.map(TransactionDocument::toDomain);
	}

	@Override
	public Flux<Transaction> ListClient(String clientId) {
		return repo.findByClienteIdOrderByFechaDesc(clientId).map(TransactionDocument::toDomain);
	}
}
