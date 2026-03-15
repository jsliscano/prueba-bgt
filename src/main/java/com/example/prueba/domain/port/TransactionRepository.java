package com.example.prueba.domain.port;

import com.example.prueba.domain.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionRepository {

	Mono<Transaction> saveTransaction(Transaction transaction);

	Flux<Transaction> ListClient(String clientId);
}
