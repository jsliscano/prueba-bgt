package com.example.prueba.domain.usecase;

import com.example.prueba.infrastructure.entrypoint.exception.ResourceNotFoundException;
import com.example.prueba.infrastructure.entrypoint.exception.InsufficientBalanceException;
import com.example.prueba.domain.model.Client;
import com.example.prueba.domain.model.Fund;
import com.example.prueba.domain.model.SubscribeResult;
import com.example.prueba.domain.model.Subscription;
import com.example.prueba.domain.model.Transaction;
import com.example.prueba.domain.model.enums.TransactionType;
import com.example.prueba.domain.model.enums.NotificationPreference;
import com.example.prueba.domain.port.ClientRepository;
import com.example.prueba.domain.port.FondRepository;
import com.example.prueba.domain.port.NotificatorPort;
import com.example.prueba.domain.port.SubscriptionRepository;
import com.example.prueba.domain.port.TransactionRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public class SubscribeFundUseCase {

	private final ClientRepository clientRepository;
	private final FondRepository fondRepository;
	private final SubscriptionRepository subscriptionRepository;
	private final TransactionRepository transactionRepository;
	private final NotificatorPort notificatorPort;

	public Mono<SubscribeResult> execute(String clientId, Long fondId, BigDecimal amount) {
		return clientRepository.searchById(clientId)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("Cliente no encontrado: " + clientId)))
				.zipWith(fondRepository.searchByIdFound(fondId)
						.switchIfEmpty(Mono.error(new ResourceNotFoundException("Fondo no encontrado: " + fondId))))
				.flatMap(tuple -> {
					Client client = tuple.getT1();
					Fund fund = tuple.getT2();
					if (!fund.meetsMinimumAmount(amount)) {
						return Mono.<SubscribeResult>error(new IllegalArgumentException("El monto debe ser al menos " + fund.getMinimumAmount() + " para el fund " + fund.getName()));
					}
					if (!client.hasSufficientBalance(amount)) {
						return Mono.<SubscribeResult>error(new InsufficientBalanceException(fund.getName(), client.getAmount()));
					}
					BigDecimal newBalance = client.getAmount().subtract(amount);
					Client  updatedclient = Client.builder()
							.id(client.getId())
							.name(client.getName())
							.amount(newBalance)
							.notificationPreference(client.getNotificationPreference())
							.email(client.getEmail())
							.phone(client.getPhone())
							.build();
					String transactionId = UUID.randomUUID().toString();
					Transaction transaction = Transaction.builder()
							.id(transactionId)
							.clientId(clientId)
							.fondId(fondId)
							.type(TransactionType.APERTURA)
							.amount(amount)
							.date(Instant.now())
							.build();
					String subscriptionId = UUID.randomUUID().toString();
					Subscription subscription = Subscription.builder()
							.id(subscriptionId)
							.clientId(clientId)
							.fondId(fondId)
							.amountInvolved(amount)
							.active(true)
							.build();
					String destination = client.getNotificationPreference() == NotificationPreference.EMAIL
							? client.getEmail()
							: client.getPhone();
					Mono<Void> notificar = (destination != null && !destination.isBlank())
							? notificatorPort.notifySubscription(client.getNotificationPreference(), destination, fund.getName())
							: Mono.empty();
					SubscribeResult result = SubscribeResult.builder().transaction(transaction).remainingBalance(newBalance).build();
					return clientRepository.saveClient( updatedclient)
							.then(transactionRepository.saveTransaction(transaction))
							.flatMap(t -> subscriptionRepository.saveSubscription(subscription).thenReturn(t))
							.flatMap(t -> notificar.thenReturn(result));
				});
	}
}
