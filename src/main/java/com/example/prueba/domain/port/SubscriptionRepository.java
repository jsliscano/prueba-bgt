package com.example.prueba.domain.port;

import com.example.prueba.domain.model.Subscription;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SubscriptionRepository {

	Mono<Subscription> saveSubscription(Subscription subscription);

	Mono<Subscription> searchByClientAndFund(String clientId, Long fondId);

	Flux<Subscription> listActiveByCustomer(String clientId);
}
