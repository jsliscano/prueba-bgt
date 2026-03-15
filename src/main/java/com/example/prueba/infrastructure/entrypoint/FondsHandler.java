package com.example.prueba.infrastructure.entrypoint;

import com.example.prueba.infrastructure.entrypoint.exception.ResourceNotFoundException;
import com.example.prueba.infrastructure.entrypoint.exception.InsufficientBalanceException;
import com.example.prueba.domain.model.Client;
import com.example.prueba.domain.model.Fund;
import com.example.prueba.domain.model.Transaction;
import com.example.prueba.domain.port.ClientRepository;
import com.example.prueba.domain.port.FondRepository;
import com.example.prueba.infrastructure.entrypoint.dto.request.CancelRequest;
import com.example.prueba.infrastructure.entrypoint.dto.request.CreateClientRequest;
import com.example.prueba.infrastructure.entrypoint.dto.request.CreateFundRequest;
import com.example.prueba.infrastructure.entrypoint.dto.request.TransactionHistoryRequest;
import com.example.prueba.infrastructure.entrypoint.dto.request.RechargeBalanceRequest;
import com.example.prueba.infrastructure.entrypoint.dto.request.SubscribeRequest;
import com.example.prueba.infrastructure.entrypoint.dto.response.ApiResponse;
import com.example.prueba.infrastructure.entrypoint.dto.response.InsufficientBalanceData;
import com.example.prueba.domain.usecase.CancelSubscriptionUseCase;
import com.example.prueba.domain.usecase.BalanceUseCase;
import com.example.prueba.domain.usecase.SubscribeFundUseCase;
import com.example.prueba.domain.usecase.TransactionHistoryUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FondsHandler {

	private static final BigDecimal INITIAL_CLIENT_BALANCE = new BigDecimal("500000");

	private final SubscribeFundUseCase subscribeFundUseCase;
	private final CancelSubscriptionUseCase cancelSubscriptionUseCase;
	private final TransactionHistoryUseCase transactionHistoryUseCase;
	private final BalanceUseCase topUpBalanceUseCase;
	private final FondRepository fondRepository;
	private final ClientRepository clientRepository;

	public Mono<ResponseEntity<ApiResponse<List<Fund>>>> listFonds() {
		return fondRepository.listAllFunds().collectList()
				.map(lista -> ResponseEntity.ok(ApiResponse.ok(lista, "Búsqueda realizada con éxito. Fondos disponibles.")));
	}

	public Mono<ResponseEntity<ApiResponse<Fund>>> createFond(CreateFundRequest request) {
		Fund fond = Fund.builder()
				.id(request.getId())
				.name(request.getName())
				.minimumAmount(request.getMinimumAmount())
				.category(request.getCategory())
				.build();
		return fondRepository.saveFund(fond)
				.map(creado -> ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(creado, "Fondo creado correctamente.")))
				.onErrorResume(ResourceNotFoundException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.<Fund>error(e.getMessage()))));
	}

	public Mono<ResponseEntity<ApiResponse<Void>>> deleteFond(Long id) {
		return fondRepository.searchByIdFound(id)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("Fondo no encontrado: " + id)))
				.flatMap(f -> fondRepository.deleteByIdFound(id))
				.then(Mono.fromCallable(() -> ResponseEntity.ok(ApiResponse.<Void>ok(null, "Fondo eliminado correctamente."))))
				.onErrorResume(ResourceNotFoundException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(ApiResponse.<Void>error(e.getMessage()))));
	}

	public Mono<ResponseEntity<ApiResponse<Client>>> createClient(CreateClientRequest request) {
		Mono<String> idMono = (request.getId() != null && !request.getId().isBlank())
				? Mono.just(request.getId())
				: clientRepository.nextClientId();
		return idMono.flatMap(id -> {
			Client client = Client.builder()
					.id(id)
					.name(request.getName())
					.amount(INITIAL_CLIENT_BALANCE)
					.notificationPreference(request.getNotificationPreference())
					.email(request.getEmail())
					.phone(request.getPhone())
					.build();
			return clientRepository.saveClient(client)
					.map(creado -> ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(creado, "Cliente creado correctamente. Saldo inicial COP 500.000.")));
		});
	}

	public Mono<ResponseEntity<ApiResponse<?>>> subscriber(SubscribeRequest request) {
		return subscribeFundUseCase.execute(request.getClientId(), request.getFondId(), request.getAmount())
				.<ResponseEntity<ApiResponse<?>>>map(r -> ResponseEntity.status(HttpStatus.CREATED).body(
						ApiResponse.created(r, "Suscripción al fondo realizada con éxito. Saldo restante: COP " + r.getRemainingBalance() + ". Se enviará notificación según su preferencia.")))
				.onErrorResume(InsufficientBalanceException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
						.body((ApiResponse<?>) new ApiResponse<>(false, e.getMessage(), new InsufficientBalanceData(e.getAvailableBalance())))))
				.onErrorResume(ResourceNotFoundException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()))))
				.onErrorResume(IllegalArgumentException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()))));
	}

	public Mono<ResponseEntity<ApiResponse<Transaction>>> cancelSubscription(CancelRequest request) {
		return cancelSubscriptionUseCase.execute(request.getClientId(), request.getFondId())
				.map(t -> ResponseEntity.ok(ApiResponse.ok(t, "Suscripción cancelada correctamente. El valor de vinculación ha sido devuelto a su saldo.")))
				.onErrorResume(ResourceNotFoundException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.<Transaction>error(e.getMessage()))));
	}

	public Mono<ResponseEntity<ApiResponse<List<Transaction>>>> transactionHistory(TransactionHistoryRequest request) {
		return transactionHistoryUseCase.execute(request.getClientId())
				.map(lista -> ResponseEntity.ok(ApiResponse.ok(lista, "Historial de transacciones obtenido correctamente.")))
				.onErrorResume(ResourceNotFoundException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.<List<Transaction>>error(e.getMessage()))));
	}

	public Mono<ResponseEntity<ApiResponse<Client>>> Balance(String clienteId) {
		return clientRepository.searchById(clienteId)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("Cliente no encontrado: " + clienteId)))
				.map(c -> ResponseEntity.ok(ApiResponse.ok(c, "Saldo del cliente obtenido correctamente.")))
				.onErrorResume(ResourceNotFoundException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.<Client>error(e.getMessage()))));
	}

	public Mono<ResponseEntity<ApiResponse<Client>>> rechargeBalance(RechargeBalanceRequest request) {
		return topUpBalanceUseCase.execute(request.getClientId(), request.getAmount())
				.map(c -> ResponseEntity.ok(ApiResponse.ok(c, "Saldo recargado correctamente. Nuevo saldo: COP " + c.getAmount())))
				.onErrorResume(ResourceNotFoundException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.<Client>error(e.getMessage()))))
				.onErrorResume(IllegalArgumentException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.<Client>error(e.getMessage()))));
	}
}
