package com.example.prueba.domain.usecase;

import java.math.BigDecimal;

import com.example.prueba.infrastructure.entrypoint.exception.ResourceNotFoundException;
import com.example.prueba.domain.model.Client;
import com.example.prueba.domain.model.enums.NotificationPreference;
import com.example.prueba.domain.port.ClientRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("BalanceUseCase")
class BalanceUseCaseTest {

	private ClientRepository clientRepository;
	private BalanceUseCase useCase;

	@BeforeEach
	void setUp() {
		clientRepository = mock(ClientRepository.class);
		useCase = new BalanceUseCase(clientRepository);
	}

	@Test
	@DisplayName("updates client balance and returns saved client")
	void success() {
		Client client = Client.builder()
				.id("c1")
				.name("Test")
				.amount(new BigDecimal("100000"))
				.notificationPreference(NotificationPreference.EMAIL)
				.email("a@b.com")
				.phone(null)
				.build();
		when(clientRepository.searchById("c1")).thenReturn(Mono.just(client));
		when(clientRepository.saveClient(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));

		StepVerifier.create(useCase.execute("c1", new BigDecimal("50000")))
				.expectNextMatches(c -> c.getAmount().compareTo(new BigDecimal("150000")) == 0)
				.verifyComplete();

		verify(clientRepository).saveClient(any());
	}

	@Test
	@DisplayName("throws ResourceNotFoundException when client not found")
	void clientNotFound() {
		when(clientRepository.searchById("c1")).thenReturn(Mono.empty());

		StepVerifier.create(useCase.execute("c1", new BigDecimal("100")))
				.expectErrorMatches(e -> e instanceof ResourceNotFoundException
						&& e.getMessage().contains("Cliente no encontrado"))
				.verify();
	}

	@Test
	@DisplayName("throws IllegalArgumentException when amount is null")
	void amountNull() {
		StepVerifier.create(useCase.execute("c1", null))
				.expectErrorMatches(e -> e instanceof IllegalArgumentException
						&& e.getMessage().contains("monto"))
				.verify();
	}

	@Test
	@DisplayName("throws IllegalArgumentException when amount is zero")
	void amountZero() {
		StepVerifier.create(useCase.execute("c1", BigDecimal.ZERO))
				.expectErrorMatches(e -> e instanceof IllegalArgumentException
						&& e.getMessage().contains("monto"))
				.verify();
	}

	@Test
	@DisplayName("throws IllegalArgumentException when amount is negative")
	void amountNegative() {
		StepVerifier.create(useCase.execute("c1", new BigDecimal("-10")))
				.expectErrorMatches(e -> e instanceof IllegalArgumentException)
				.verify();
	}
}
