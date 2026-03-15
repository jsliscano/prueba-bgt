package com.example.prueba.domain.usecase;

import com.example.prueba.infrastructure.entrypoint.exception.ResourceNotFoundException;
import com.example.prueba.domain.model.Client;
import com.example.prueba.domain.model.Subscription;
import com.example.prueba.domain.model.Transaction;
import com.example.prueba.domain.model.enums.TransactionType;
import com.example.prueba.domain.port.ClientRepository;
import com.example.prueba.domain.port.SubscriptionRepository;
import com.example.prueba.domain.port.TransactionRepository;
import java.time.Instant;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CancelSubscriptionUseCase {

	private final ClientRepository clientRepository;
	private final SubscriptionRepository subscriptionRepository;
	private final TransactionRepository transactionRepository;

	public Mono<Transaction> execute(String clientId, Long fondId) {
		Mono<Client> clienteMono = clientRepository.searchById(clientId)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("Cliente no encontrado: " + clientId)));
		Mono<Subscription> suscripcionMono = subscriptionRepository.searchByClientAndFund(clientId, fondId)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("Suscripción activa no encontrada para cliente " + clientId + " y fondo " + fondId)));
		return Mono.zip(clienteMono, suscripcionMono)
				.flatMap(tuple -> {
					Client client = tuple.getT1();
					Subscription subscription = tuple.getT2();
					java.math.BigDecimal newBalance = client.getAmount().add(subscription.getAmountInvolved());
					Client  updatedClient = Client.builder()
							.id(client.getId())
							.name(client.getName())
							.amount(newBalance)
							.notificationPreference(client.getNotificationPreference())
							.email(client.getEmail())
							.phone(client.getPhone())
							.build();
					Subscription canceled = Subscription.builder()
							.id(subscription.getId())
							.clientId(subscription.getClientId())
							.fondId(subscription.getFondId())
							.amountInvolved(subscription.getAmountInvolved())
							.active(false)
							.build();
					String transactionId = UUID.randomUUID().toString();
					Transaction transaction = Transaction.builder()
							.id(transactionId)
							.clientId(clientId)
							.fondId(fondId)
							.type(TransactionType.CANCELACION)
							.amount(subscription.getAmountInvolved())
							.date(Instant.now())
							.build();
					return clientRepository.saveClient( updatedClient)
							.then(subscriptionRepository.saveSubscription(canceled))
							.then(transactionRepository.saveTransaction(transaction));
				});
	}
}
