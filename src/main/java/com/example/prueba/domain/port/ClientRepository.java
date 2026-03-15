package com.example.prueba.domain.port;

import com.example.prueba.domain.model.Client;
import reactor.core.publisher.Mono;

public interface ClientRepository {

	Mono<Client> searchById(String id);

	Mono<Client> saveClient(Client client);
	
	Mono<String> nextClientId();
}
