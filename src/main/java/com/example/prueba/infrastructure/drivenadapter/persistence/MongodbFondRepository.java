package com.example.prueba.infrastructure.drivenadapter.persistence;

import org.springframework.stereotype.Component;

import com.example.prueba.domain.model.Fund;
import com.example.prueba.domain.port.FondRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class MongodbFondRepository implements FondRepository {

	private final SpringDataFondRepository repo;

	public MongodbFondRepository(SpringDataFondRepository repo) {
		this.repo = repo;
	}

	@Override
	public Mono<Fund> searchByIdFound(Long id) {
		return repo.findById(id).map(FondDocument::toDomain);
	}

	@Override
	public Flux<Fund> listAllFunds() {
		return repo.findAll().map(FondDocument::toDomain);
	}

	@Override
	public Mono<Fund> saveFund(Fund fond) {
		return Mono.just(FondDocument.from(fond))
				.flatMap(repo::save)
				.map(FondDocument::toDomain);
	}

	@Override
	public Mono<Void> deleteByIdFound(Long id) {
		return repo.deleteById(id);
	}
}
