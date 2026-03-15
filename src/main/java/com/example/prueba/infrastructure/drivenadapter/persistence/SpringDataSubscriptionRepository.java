package com.example.prueba.infrastructure.drivenadapter.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;

public interface SpringDataSubscriptionRepository extends ReactiveMongoRepository<SubscriptionDocument, String> {
	Flux<SubscriptionDocument> findByClienteIdAndFondoIdAndActivaTrue(String clienteId, Long fondoId);
	Flux<SubscriptionDocument> findByClienteIdAndActivaTrue(String clienteId);
}
