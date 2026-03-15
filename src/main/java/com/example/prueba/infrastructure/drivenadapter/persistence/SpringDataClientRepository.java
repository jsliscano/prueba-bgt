package com.example.prueba.infrastructure.drivenadapter.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface SpringDataClientRepository extends ReactiveMongoRepository<ClientDocument, String> {
}
