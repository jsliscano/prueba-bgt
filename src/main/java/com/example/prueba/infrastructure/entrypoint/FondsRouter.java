package com.example.prueba.infrastructure.entrypoint;

import java.util.List;

import com.example.prueba.domain.model.Client;
import com.example.prueba.domain.model.Fund;
import com.example.prueba.domain.model.Transaction;
import com.example.prueba.infrastructure.entrypoint.dto.request.CancelRequest;
import com.example.prueba.infrastructure.entrypoint.dto.request.CreateClientRequest;
import com.example.prueba.infrastructure.entrypoint.dto.request.CreateFundRequest;
import com.example.prueba.infrastructure.entrypoint.dto.request.TransactionHistoryRequest;
import com.example.prueba.infrastructure.entrypoint.dto.request.RechargeBalanceRequest;
import com.example.prueba.infrastructure.entrypoint.dto.request.SubscribeRequest;
import com.example.prueba.infrastructure.entrypoint.dto.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/fondos")
@RequiredArgsConstructor
public class FondsRouter {

	private final FondsHandler handler;

	@GetMapping
	public Mono<ResponseEntity<ApiResponse<List<Fund>>>> listFonds() {
		return handler.listFonds();
	}

	@PostMapping
	public Mono<ResponseEntity<ApiResponse<Fund>>> createFond(@Valid @RequestBody CreateFundRequest request) {
		return handler.createFond(request);
	}

	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<ApiResponse<Void>>> deleteFond(@PathVariable Long id) {
		return handler.deleteFond(id);
	}

	@PostMapping("/clientes")
	public Mono<ResponseEntity<ApiResponse<Client>>> createClient(@Valid @RequestBody CreateClientRequest request) {
		return handler.createClient(request);
	}

	@PostMapping("/suscribir")
	public Mono<ResponseEntity<ApiResponse<?>>> subscriber(@Valid @RequestBody SubscribeRequest request) {
		return handler.subscriber(request);
	}

	@PostMapping("/cancelar")
	public Mono<ResponseEntity<ApiResponse<Transaction>>> cancelSubscription(@Valid @RequestBody CancelRequest request) {
		return handler.cancelSubscription(request);
	}

	@PostMapping("/clientes/transacciones")
	public Mono<ResponseEntity<ApiResponse<List<Transaction>>>> transactionHistory(@Valid @RequestBody TransactionHistoryRequest request) {
		return handler.transactionHistory(request);
	}

	@GetMapping("/clientes/{clienteId}/saldo")
	public Mono<ResponseEntity<ApiResponse<Client>>> Balance(@PathVariable String clienteId) {
		return handler.Balance(clienteId);
	}

	@PostMapping("/clientes/recargar")
	public Mono<ResponseEntity<ApiResponse<Client>>> recargarSaldo(@Valid @RequestBody RechargeBalanceRequest request) {
		return handler.rechargeBalance(request);
	}
}
