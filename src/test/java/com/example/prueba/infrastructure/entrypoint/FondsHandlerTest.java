package com.example.prueba.infrastructure.entrypoint;

import java.math.BigDecimal;
import java.util.List;

import com.example.prueba.domain.model.Client;
import com.example.prueba.domain.model.Fund;
import com.example.prueba.domain.model.SubscribeResult;
import com.example.prueba.domain.model.Transaction;
import com.example.prueba.domain.model.enums.FundCategory;
import com.example.prueba.domain.model.enums.TransactionType;
import com.example.prueba.domain.model.enums.NotificationPreference;
import com.example.prueba.domain.port.ClientRepository;
import com.example.prueba.domain.port.FondRepository;
import com.example.prueba.infrastructure.entrypoint.dto.request.CancelRequest;
import com.example.prueba.infrastructure.entrypoint.dto.request.CreateClientRequest;
import com.example.prueba.infrastructure.entrypoint.dto.request.CreateFundRequest;
import com.example.prueba.infrastructure.entrypoint.dto.request.RechargeBalanceRequest;
import com.example.prueba.infrastructure.entrypoint.dto.request.SubscribeRequest;
import com.example.prueba.infrastructure.entrypoint.dto.request.TransactionHistoryRequest;
import com.example.prueba.infrastructure.entrypoint.exception.InsufficientBalanceException;
import com.example.prueba.infrastructure.entrypoint.exception.ResourceNotFoundException;
import com.example.prueba.domain.usecase.BalanceUseCase;
import com.example.prueba.domain.usecase.CancelSubscriptionUseCase;
import com.example.prueba.domain.usecase.SubscribeFundUseCase;
import com.example.prueba.domain.usecase.TransactionHistoryUseCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("FondsHandler")
public class FondsHandlerTest {

	private SubscribeFundUseCase subscribeFundUseCase;
	private CancelSubscriptionUseCase cancelSubscriptionUseCase;
	private TransactionHistoryUseCase transactionHistoryUseCase;
	private BalanceUseCase balanceUseCase;
	private FondRepository fondRepository;
	private ClientRepository clientRepository;
	private FondsHandler handler;

	@BeforeEach
	void setUp() {
		subscribeFundUseCase = mock(SubscribeFundUseCase.class);
		cancelSubscriptionUseCase = mock(CancelSubscriptionUseCase.class);
		transactionHistoryUseCase = mock(TransactionHistoryUseCase.class);
		balanceUseCase = mock(BalanceUseCase.class);
		fondRepository = mock(FondRepository.class);
		clientRepository = mock(ClientRepository.class);
		handler = new FondsHandler(subscribeFundUseCase, cancelSubscriptionUseCase, transactionHistoryUseCase,
				balanceUseCase, fondRepository, clientRepository);
	}

	@Nested
	@DisplayName("listFonds")
	class ListFonds {
		@Test
		@DisplayName("returns 200 and list of funds")
		void success() {
			Fund fund = Fund.builder().id(1L).name("F1").minimumAmount(BigDecimal.TEN).category(FundCategory.FPV).build();
			when(fondRepository.listAllFunds()).thenReturn(Flux.just(fund));

			StepVerifier.create(handler.listFonds())
					.expectNextMatches(r -> r.getStatusCode().is2xxSuccessful()
							&& r.getBody() != null
							&& r.getBody().getData() != null
							&& r.getBody().getData().size() == 1)
					.verifyComplete();
		}
	}

	@Nested
	@DisplayName("createFond")
	class CreateFond {
		@Test
		@DisplayName("returns 201 when fund is saved")
		void success() {
			CreateFundRequest request = new CreateFundRequest();
			request.setId(1L);
			request.setName("FondoX");
			request.setMinimumAmount(new BigDecimal("100000"));
			request.setCategory(FundCategory.FPV);
			Fund saved = Fund.builder().id(1L).name("FondoX").minimumAmount(new BigDecimal("100000")).category(FundCategory.FPV).build();
			when(fondRepository.saveFund(any())).thenReturn(Mono.just(saved));

			StepVerifier.create(handler.createFond(request))
					.expectNextMatches(r -> r.getStatusCodeValue() == 201 && r.getBody() != null && r.getBody().isSuccess())
					.verifyComplete();
		}
	}

	@Nested
	@DisplayName("deleteFond")
	class DeleteFond {
		@Test
		@DisplayName("returns 200 when fund exists and is deleted")
		void success() {
			Fund fund = Fund.builder().id(1L).name("F").minimumAmount(BigDecimal.ONE).category(FundCategory.FPV).build();
			when(fondRepository.searchByIdFound(1L)).thenReturn(Mono.just(fund));
			when(fondRepository.deleteByIdFound(1L)).thenReturn(Mono.empty());

			StepVerifier.create(handler.deleteFond(1L))
					.expectNextMatches(r -> r.getStatusCode().is2xxSuccessful())
					.verifyComplete();
		}

		@Test
		@DisplayName("returns 404 when fund not found")
		void notFound() {
			when(fondRepository.searchByIdFound(999L)).thenReturn(Mono.empty());

			StepVerifier.create(handler.deleteFond(999L))
					.expectNextMatches(r -> r.getStatusCodeValue() == 404 && r.getBody() != null && !r.getBody().isSuccess())
					.verifyComplete();
		}
	}

	@Nested
	@DisplayName("createClient")
	class CreateClient {
		@Test
		@DisplayName("returns 201 with generated id when id not provided")
		void withGeneratedId() {
			CreateClientRequest request = new CreateClientRequest();
			request.setName("Vicente");
			request.setNotificationPreference(NotificationPreference.EMAIL);
			request.setEmail("v@b.com");
			request.setPhone("3209771777");
			when(clientRepository.nextClientId()).thenReturn(Mono.just("cliente-001"));
			when(clientRepository.saveClient(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));

			StepVerifier.create(handler.createClient(request))
					.expectNextMatches(r -> r.getStatusCodeValue() == 201
							&& r.getBody() != null
							&& r.getBody().getData() != null
							&& "cliente-001".equals(r.getBody().getData().getId()))
					.verifyComplete();
		}

		@Test
		@DisplayName("returns 201 with provided id when id is set")
		void withProvidedId() {
			CreateClientRequest request = new CreateClientRequest();
			request.setId("cliente-custom");
			request.setName("Vicente");
			request.setNotificationPreference(NotificationPreference.EMAIL);
			when(clientRepository.saveClient(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));

			StepVerifier.create(handler.createClient(request))
					.expectNextMatches(r -> r.getBody() != null && "cliente-custom".equals(r.getBody().getData().getId()))
					.verifyComplete();
			verify(clientRepository).saveClient(any());
		}
	}

	@Nested
	@DisplayName("subscriber")
	class Subscriber {
		@Test
		@DisplayName("returns 201 when subscription succeeds")
		void success() {
			SubscribeRequest request = new SubscribeRequest();
			request.setClientId("c1");
			request.setFondId(1L);
			request.setAmount(new BigDecimal("100000"));
			Transaction tx = Transaction.builder().id("tx1").clientId("c1").fondId(1L).type(TransactionType.APERTURA).amount(new BigDecimal("100000")).date(java.time.Instant.now()).build();
			SubscribeResult result = SubscribeResult.builder().transaction(tx).remainingBalance(new BigDecimal("400000")).build();
			when(subscribeFundUseCase.execute(eq("c1"), eq(1L), any())).thenReturn(Mono.just(result));

			StepVerifier.create(handler.subscriber(request))
					.expectNextMatches(r -> r.getStatusCodeValue() == 201 && r.getBody() != null && r.getBody().isSuccess())
					.verifyComplete();
		}

		@Test
		@DisplayName("returns 422 when InsufficientBalanceException")
		void insufficientBalance() {
			SubscribeRequest request = new SubscribeRequest();
			request.setClientId("c1");
			request.setFondId(1L);
			request.setAmount(new BigDecimal("100000"));
			when(subscribeFundUseCase.execute(any(), anyLong(), any()))
					.thenReturn(Mono.error(new InsufficientBalanceException("F1", new BigDecimal("50000"))));

			StepVerifier.create(handler.subscriber(request))
					.expectNextMatches(r -> r.getStatusCodeValue() == 422 && r.getBody() != null && !r.getBody().isSuccess())
					.verifyComplete();
		}

		@Test
		@DisplayName("returns 404 when ResourceNotFoundException")
		void resourceNotFound() {
			SubscribeRequest request = new SubscribeRequest();
			request.setClientId("c1");
			request.setFondId(1L);
			request.setAmount(new BigDecimal("100000"));
			when(subscribeFundUseCase.execute(any(), anyLong(), any()))
					.thenReturn(Mono.error(new ResourceNotFoundException("Cliente no encontrado")));

			StepVerifier.create(handler.subscriber(request))
					.expectNextMatches(r -> r.getStatusCodeValue() == 404)
					.verifyComplete();
		}

		@Test
		@DisplayName("returns 400 when IllegalArgumentException")
		void invalidArgument() {
			SubscribeRequest request = new SubscribeRequest();
			request.setClientId("c1");
			request.setFondId(1L);
			request.setAmount(new BigDecimal("1000"));
			when(subscribeFundUseCase.execute(any(), anyLong(), any()))
					.thenReturn(Mono.error(new IllegalArgumentException("Monto mínimo no alcanzado")));

			StepVerifier.create(handler.subscriber(request))
					.expectNextMatches(r -> r.getStatusCodeValue() == 400)
					.verifyComplete();
		}
	}

	@Nested
	@DisplayName("cancelSubscription")
	class CancelSubscription {
		@Test
		@DisplayName("returns 200 when cancel succeeds")
		void success() {
			CancelRequest request = new CancelRequest();
			request.setClientId("c1");
			request.setFondId(1L);
			Transaction tx = Transaction.builder().id("tx1").clientId("c1").fondId(1L).type(TransactionType.CANCELACION).amount(BigDecimal.TEN).date(java.time.Instant.now()).build();
			when(cancelSubscriptionUseCase.execute("c1", 1L)).thenReturn(Mono.just(tx));

			StepVerifier.create(handler.cancelSubscription(request))
					.expectNextMatches(r -> r.getStatusCode().is2xxSuccessful() && r.getBody().getData() != null)
					.verifyComplete();
		}

		@Test
		@DisplayName("returns 404 when subscription not found")
		void notFound() {
			CancelRequest request = new CancelRequest();
			request.setClientId("c1");
			request.setFondId(1L);
			when(cancelSubscriptionUseCase.execute("c1", 1L))
					.thenReturn(Mono.error(new ResourceNotFoundException("Suscripción no encontrada")));

			StepVerifier.create(handler.cancelSubscription(request))
					.expectNextMatches(r -> r.getStatusCodeValue() == 404)
					.verifyComplete();
		}
	}

	@Nested
	@DisplayName("transactionHistory")
	class TransactionHistory {
		@Test
		@DisplayName("returns 200 with list of transactions")
		void success() {
			TransactionHistoryRequest request = new TransactionHistoryRequest();
			request.setClientId("c1");
			Transaction tx = Transaction.builder().id("tx1").clientId("c1").fondId(1L).type(TransactionType.APERTURA).amount(BigDecimal.ONE).date(java.time.Instant.now()).build();
			when(transactionHistoryUseCase.execute("c1")).thenReturn(Mono.just(List.of(tx)));

			StepVerifier.create(handler.transactionHistory(request))
					.expectNextMatches(r -> r.getStatusCode().is2xxSuccessful()
							&& r.getBody().getData().size() == 1)
					.verifyComplete();
		}

		@Test
		@DisplayName("returns 404 when client not found")
		void notFound() {
			TransactionHistoryRequest request = new TransactionHistoryRequest();
			request.setClientId("c1");
			when(transactionHistoryUseCase.execute("c1"))
					.thenReturn(Mono.error(new ResourceNotFoundException("Cliente no encontrado")));

			StepVerifier.create(handler.transactionHistory(request))
					.expectNextMatches(r -> r.getStatusCodeValue() == 404)
					.verifyComplete();
		}
	}

	@Nested
	@DisplayName("Balance")
	class Balance {
		@Test
		@DisplayName("returns 200 with client when found")
		void success() {
			Client client = Client.builder().id("c1").name("V").amount(new BigDecimal("500000")).notificationPreference(NotificationPreference.EMAIL).email("v@b.com").phone(null).build();
			when(clientRepository.searchById("c1")).thenReturn(Mono.just(client));

			StepVerifier.create(handler.Balance("c1"))
					.expectNextMatches(r -> r.getStatusCode().is2xxSuccessful() && r.getBody().getData().getId().equals("c1"))
					.verifyComplete();
		}

		@Test
		@DisplayName("returns 404 when client not found")
		void notFound() {
			when(clientRepository.searchById("c1")).thenReturn(Mono.empty());

			StepVerifier.create(handler.Balance("c1"))
					.expectNextMatches(r -> r.getStatusCodeValue() == 404 && r.getBody() != null && !r.getBody().isSuccess())
					.verifyComplete();
		}
	}

	@Nested
	@DisplayName("rechargeBalance")
	class RechargeBalance {
		@Test
		@DisplayName("returns 200 with updated client")
		void success() {
			RechargeBalanceRequest request = new RechargeBalanceRequest();
			request.setClientId("c1");
			request.setAmount(new BigDecimal("100000"));
			Client updated = Client.builder().id("c1").name("V").amount(new BigDecimal("600000")).build();
			when(balanceUseCase.execute("c1", new BigDecimal("100000"))).thenReturn(Mono.just(updated));

			StepVerifier.create(handler.rechargeBalance(request))
					.expectNextMatches(r -> r.getStatusCode().is2xxSuccessful()
							&& r.getBody().getData().getAmount().compareTo(new BigDecimal("600000")) == 0)
					.verifyComplete();
		}

		@Test
		@DisplayName("returns 404 when client not found")
		void notFound() {
			RechargeBalanceRequest request = new RechargeBalanceRequest();
			request.setClientId("c1");
			request.setAmount(new BigDecimal("100"));
			when(balanceUseCase.execute(any(), any()))
					.thenReturn(Mono.error(new ResourceNotFoundException("Cliente no encontrado")));

			StepVerifier.create(handler.rechargeBalance(request))
					.expectNextMatches(r -> r.getStatusCodeValue() == 404)
					.verifyComplete();
		}

		@Test
		@DisplayName("returns 400 when invalid amount")
		void invalidAmount() {
			RechargeBalanceRequest request = new RechargeBalanceRequest();
			request.setClientId("c1");
			request.setAmount(new BigDecimal("-1"));
			when(balanceUseCase.execute(any(), any()))
					.thenReturn(Mono.error(new IllegalArgumentException("Monto inválido")));

			StepVerifier.create(handler.rechargeBalance(request))
					.expectNextMatches(r -> r.getStatusCodeValue() == 400)
					.verifyComplete();
		}
	}
}
