package com.example.prueba.domain.usecase;

import java.math.BigDecimal;
import java.time.Instant;

import com.example.prueba.infrastructure.entrypoint.exception.ResourceNotFoundException;
import com.example.prueba.domain.model.Client;
import com.example.prueba.domain.model.Subscription;
import com.example.prueba.domain.model.Transaction;
import com.example.prueba.domain.model.enums.NotificationPreference;
import com.example.prueba.domain.model.enums.TransactionType;
import com.example.prueba.domain.port.ClientRepository;
import com.example.prueba.domain.port.SubscriptionRepository;
import com.example.prueba.domain.port.TransactionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("CancelSubscriptionUseCase")
class CancelSubscriptionUseCaseTest {

	private ClientRepository clientRepository;
	private SubscriptionRepository subscriptionRepository;
	private TransactionRepository transactionRepository;
	private CancelSubscriptionUseCase useCase;

	@BeforeEach
	void setUp() {
		clientRepository = mock(ClientRepository.class);
		subscriptionRepository = mock(SubscriptionRepository.class);
		transactionRepository = mock(TransactionRepository.class);
		useCase = new CancelSubscriptionUseCase(clientRepository, subscriptionRepository, transactionRepository);
	}

	@Test
	@DisplayName("returns transaction and updates client balance when subscription exists")
	void cancelSuccess() {
		Client client = Client.builder()
				.id("c1")
				.name("Test")
				.amount(new BigDecimal("100000"))
				.notificationPreference(NotificationPreference.EMAIL)
				.email("a@b.com")
				.phone(null)
				.build();
		Subscription subscription = Subscription.builder()
				.id("sub1")
				.clientId("c1")
				.fondId(1L)
				.amountInvolved(new BigDecimal("50000"))
				.active(true)
				.build();

		when(clientRepository.searchById("c1")).thenReturn(Mono.just(client));
		when(subscriptionRepository.searchByClientAndFund("c1", 1L)).thenReturn(Mono.just(subscription));
		when(clientRepository.saveClient(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
		when(subscriptionRepository.saveSubscription(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
		when(transactionRepository.saveTransaction(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));

		StepVerifier.create(useCase.execute("c1", 1L))
				.expectNextMatches(t -> t.getClientId().equals("c1")
						&& t.getFondId() == 1L
						&& t.getType() == TransactionType.CANCELACION
						&& t.getAmount().compareTo(new BigDecimal("50000")) == 0)
				.verifyComplete();

		verify(clientRepository).saveClient(any());
		verify(subscriptionRepository).saveSubscription(any());
		verify(transactionRepository).saveTransaction(any());
	}

	@Test
	@DisplayName("throws ResourceNotFoundException when client not found")
	void clientNotFound() {
		when(clientRepository.searchById("c1")).thenReturn(Mono.empty());
		when(subscriptionRepository.searchByClientAndFund("c1", 1L)).thenReturn(Mono.empty());

		StepVerifier.create(useCase.execute("c1", 1L))
				.expectErrorMatches(e -> e instanceof ResourceNotFoundException
						&& e.getMessage().contains("Cliente no encontrado"))
				.verify();
	}

	@Test
	@DisplayName("throws ResourceNotFoundException when subscription not found")
	void subscriptionNotFound() {
		Client client = Client.builder()
				.id("c1")
				.name("Test")
				.amount(new BigDecimal("100000"))
				.notificationPreference(NotificationPreference.EMAIL)
				.email("a@b.com")
				.phone(null)
				.build();
		when(clientRepository.searchById("c1")).thenReturn(Mono.just(client));
		when(subscriptionRepository.searchByClientAndFund("c1", 1L)).thenReturn(Mono.empty());

		StepVerifier.create(useCase.execute("c1", 1L))
				.expectErrorMatches(e -> e instanceof ResourceNotFoundException
						&& e.getMessage().contains("Suscripción activa no encontrada"))
				.verify();
	}
}
