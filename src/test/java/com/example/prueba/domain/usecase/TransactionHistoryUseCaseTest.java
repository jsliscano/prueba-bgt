package com.example.prueba.domain.usecase;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.example.prueba.infrastructure.entrypoint.exception.ResourceNotFoundException;
import com.example.prueba.domain.model.Transaction;
import com.example.prueba.domain.model.enums.TransactionType;
import com.example.prueba.domain.port.ClientRepository;
import com.example.prueba.domain.port.TransactionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("TransactionHistoryUseCase")
class TransactionHistoryUseCaseTest {

	private ClientRepository clientRepository;
	private TransactionRepository transactionRepository;
	private TransactionHistoryUseCase useCase;

	@BeforeEach
	void setUp() {
		clientRepository = mock(ClientRepository.class);
		transactionRepository = mock(TransactionRepository.class);
		useCase = new TransactionHistoryUseCase(clientRepository, transactionRepository);
	}

	@Test
	@DisplayName("returns list of transactions when client exists")
	void success() {
		com.example.prueba.domain.model.Client client = com.example.prueba.domain.model.Client.builder()
				.id("c1")
				.name("Test")
				.amount(BigDecimal.TEN)
				.notificationPreference(com.example.prueba.domain.model.enums.NotificationPreference.EMAIL)
				.email("a@b.com")
				.phone(null)
				.build();
		Transaction tx = Transaction.builder()
				.id("tx1")
				.clientId("c1")
				.fondId(1L)
				.type(TransactionType.APERTURA)
				.amount(new BigDecimal("100"))
				.date(Instant.now())
				.build();

		when(clientRepository.searchById("c1")).thenReturn(Mono.just(client));
		when(transactionRepository.ListClient("c1")).thenReturn(Flux.just(tx));

		StepVerifier.create(useCase.execute("c1"))
				.expectNextMatches(list -> list.size() == 1 && list.get(0).getId().equals("tx1"))
				.verifyComplete();

		verify(clientRepository).searchById("c1");
		verify(transactionRepository).ListClient("c1");
	}

	@Test
	@DisplayName("throws ResourceNotFoundException when client not found")
	void clientNotFound() {
		when(clientRepository.searchById("c1")).thenReturn(Mono.empty());

		StepVerifier.create(useCase.execute("c1"))
				.expectErrorMatches(e -> e instanceof ResourceNotFoundException
						&& e.getMessage().contains("Cliente no encontrado"))
				.verify();
	}

	@Test
	@DisplayName("returns empty list when client has no transactions")
	void emptyList() {
		com.example.prueba.domain.model.Client client = com.example.prueba.domain.model.Client.builder()
				.id("c1")
				.name("Test")
				.amount(BigDecimal.ZERO)
				.notificationPreference(com.example.prueba.domain.model.enums.NotificationPreference.EMAIL)
				.email("a@b.com")
				.phone(null)
				.build();
		when(clientRepository.searchById("c1")).thenReturn(Mono.just(client));
		when(transactionRepository.ListClient("c1")).thenReturn(Flux.empty());

		StepVerifier.create(useCase.execute("c1"))
				.expectNextMatches(List::isEmpty)
				.verifyComplete();
	}
}
