package com.example.prueba.domain.usecase;

import java.math.BigDecimal;

import com.example.prueba.infrastructure.entrypoint.exception.InsufficientBalanceException;
import com.example.prueba.infrastructure.entrypoint.exception.ResourceNotFoundException;
import com.example.prueba.domain.model.Client;
import com.example.prueba.domain.model.Fund;
import com.example.prueba.domain.model.enums.NotificationPreference;
import com.example.prueba.domain.model.enums.FundCategory;
import com.example.prueba.domain.port.ClientRepository;
import com.example.prueba.domain.port.FondRepository;
import com.example.prueba.domain.port.NotificatorPort;
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

@DisplayName("SubscribeFundUseCase")
class SubscribeFundUseCaseTest {

	private ClientRepository clientRepository;
	private FondRepository fondoRepository;
	private SubscriptionRepository suscripcionRepository;
	private TransactionRepository transaccionRepository;
	private NotificatorPort notificadorPort;
	private SubscribeFundUseCase useCase;

	@BeforeEach
	void setUp() {
		clientRepository = mock(ClientRepository.class);
		fondoRepository = mock(FondRepository.class);
		suscripcionRepository = mock(SubscriptionRepository.class);
		transaccionRepository = mock(TransactionRepository.class);
		notificadorPort = mock(NotificatorPort.class);
		useCase = new SubscribeFundUseCase(clientRepository, fondoRepository, suscripcionRepository, transaccionRepository, notificadorPort);
	}

	@Test
	@DisplayName("throws InsufficientBalanceException when balance is not enough")
	void insufficientBalance() {
		Client cliente = Client.builder()
				.id("c1")
				.name("Test")
				.amount(new BigDecimal("10000"))
				.notificationPreference(NotificationPreference.EMAIL)
				.email("a@b.com")
				.phone(null)
				.build();
		Fund fondo = Fund.builder()
				.id(1L)
				.name("FPV_BTG_PACTUAL_RECAUDADORA")
				.minimumAmount(new BigDecimal("75000"))
				.category(FundCategory.FPV)
				.build();

		when(clientRepository.searchById("c1")).thenReturn(Mono.just(cliente));
		when(fondoRepository.searchByIdFound(1L)).thenReturn(Mono.just(fondo));

		StepVerifier.create(useCase.execute("c1", 1L, new BigDecimal("75000")))
				.expectErrorMatches(e -> e instanceof InsufficientBalanceException
						&& e.getMessage().contains("No tiene saldo disponible para vincularse al fondo FPV_BTG_PACTUAL_RECAUDADORA"))
				.verify();
	}

	@Test
	@DisplayName("saves transaction and subscription when balance is sufficient")
	void subscribesSuccessfully() {
		Client cliente = Client.builder()
				.id("c1")
				.name("Test")
				.amount(new BigDecimal("100000"))
				.notificationPreference(NotificationPreference.EMAIL)
				.email("a@b.com")
				.phone(null)
				.build();
		Fund fondo = Fund.builder()
				.id(1L)
				.name("FPV_BTG_PACTUAL_RECAUDADORA")
				.minimumAmount(new BigDecimal("75000"))
				.category(FundCategory.FPV)
				.build();

		when(clientRepository.searchById("c1")).thenReturn(Mono.just(cliente));
		when(fondoRepository.searchByIdFound(1L)).thenReturn(Mono.just(fondo));
		when(transaccionRepository.saveTransaction(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
		when(suscripcionRepository.saveSubscription(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
		when(clientRepository.saveClient(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
		when(notificadorPort.notifySubscription(any(), any(), any())).thenReturn(Mono.empty());

		StepVerifier.create(useCase.execute("c1", 1L, new BigDecimal("75000")))
				.expectNextMatches(r -> r.getTransaction().getAmount().compareTo(new BigDecimal("75000")) == 0
						&& r.getRemainingBalance().compareTo(new BigDecimal("25000")) == 0)
				.verifyComplete();

		verify(transaccionRepository).saveTransaction(any());
		verify(suscripcionRepository).saveSubscription(any());
		verify(notificadorPort).notifySubscription(NotificationPreference.EMAIL, "a@b.com", "FPV_BTG_PACTUAL_RECAUDADORA");
	}

	@Test
	@DisplayName("throws ResourceNotFoundException when client not found")
	void clientNotFound() {
		when(clientRepository.searchById("c1")).thenReturn(Mono.empty());
		when(fondoRepository.searchByIdFound(1L)).thenReturn(Mono.empty());

		StepVerifier.create(useCase.execute("c1", 1L, new BigDecimal("75000")))
				.expectErrorMatches(e -> e instanceof ResourceNotFoundException
						&& e.getMessage().contains("Cliente no encontrado"))
				.verify();
	}

	@Test
	@DisplayName("throws ResourceNotFoundException when fund not found")
	void fundNotFound() {
		Client client = Client.builder()
				.id("c1")
				.name("Test")
				.amount(new BigDecimal("100000"))
				.notificationPreference(NotificationPreference.EMAIL)
				.email("a@b.com")
				.phone(null)
				.build();
		when(clientRepository.searchById("c1")).thenReturn(Mono.just(client));
		when(fondoRepository.searchByIdFound(99L)).thenReturn(Mono.empty());

		StepVerifier.create(useCase.execute("c1", 99L, new BigDecimal("75000")))
				.expectErrorMatches(e -> e instanceof ResourceNotFoundException
						&& e.getMessage().contains("Fondo no encontrado"))
				.verify();
	}

	@Test
	@DisplayName("throws IllegalArgumentException when amount is below fund minimum")
	void amountBelowMinimum() {
		Client client = Client.builder()
				.id("c1")
				.name("Test")
				.amount(new BigDecimal("100000"))
				.notificationPreference(NotificationPreference.EMAIL)
				.email("a@b.com")
				.phone(null)
				.build();
		Fund fund = Fund.builder()
				.id(1L)
				.name("FPV_MIN")
				.minimumAmount(new BigDecimal("75000"))
				.category(FundCategory.FPV)
				.build();
		when(clientRepository.searchById("c1")).thenReturn(Mono.just(client));
		when(fondoRepository.searchByIdFound(1L)).thenReturn(Mono.just(fund));

		StepVerifier.create(useCase.execute("c1", 1L, new BigDecimal("50000")))
				.expectErrorMatches(e -> e instanceof IllegalArgumentException
						&& e.getMessage().contains("75000"))
				.verify();
	}
}
