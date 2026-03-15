package com.example.prueba.infrastructure.drivenadapter.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;

public interface SpringDataTransactionRepository extends ReactiveMongoRepository<TransactionDocument, String> {
	Flux<TransactionDocument> findByClienteIdOrderByFechaDesc(String clienteId);
}
