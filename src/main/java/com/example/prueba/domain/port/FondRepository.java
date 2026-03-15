package com.example.prueba.domain.port;

import com.example.prueba.domain.model.Fund;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface FondRepository {

	Mono<Fund> searchByIdFound(Long id);

	Flux<Fund> listAllFunds();

	Mono<Fund> saveFund(Fund fond);

	Mono<Void> deleteByIdFound(Long id);
}
