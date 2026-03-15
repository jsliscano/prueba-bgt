package com.example.prueba.infrastructure.drivenadapter.persistence;

import org.springframework.stereotype.Component;

import com.example.prueba.domain.model.Subscription;
import com.example.prueba.domain.port.SubscriptionRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class MongodbSubscriptionRepository implements SubscriptionRepository {

	private final SpringDataSubscriptionRepository repo;

	public MongodbSubscriptionRepository(SpringDataSubscriptionRepository repo) {
		this.repo = repo;
	}

	@Override
	public Mono<Subscription> saveSubscription(Subscription subscription) {
		return Mono.just(SubscriptionDocument.from(subscription))
				.flatMap(repo::save)
				.map(SubscriptionDocument::toDomain);
	}

	@Override
	public Mono<Subscription> searchByClientAndFund(String clientId, Long fondId) {
		return repo.findByClienteIdAndFondoIdAndActivaTrue(clientId, fondId).next().map(SubscriptionDocument::toDomain);
	}

	@Override
	public Flux<Subscription> listActiveByCustomer(String clientId) {
		return repo.findByClienteIdAndActivaTrue(clientId).map(SubscriptionDocument::toDomain);
	}
}
