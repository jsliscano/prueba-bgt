package com.example.prueba.infrastructure.entrypoint.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.example.prueba.infrastructure.entrypoint.dto.response.ApiResponse;
import com.example.prueba.infrastructure.entrypoint.dto.response.InsufficientBalanceData;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InsufficientBalanceException.class)
	public ResponseEntity<ApiResponse<InsufficientBalanceData>> insufficientBalance(InsufficientBalanceException e) {
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body(new ApiResponse<>(false, e.getMessage(), new InsufficientBalanceData(e.getAvailableBalance())));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> resourceNotFound(ResourceNotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<Void>> invalidArgument(IllegalArgumentException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
	}

	@ExceptionHandler(WebExchangeBindException.class)
	public ResponseEntity<ApiResponse<Void>> validation(WebExchangeBindException e) {
		String message = e.getBindingResult().getFieldErrors().stream()
				.map(err -> err.getField() + ": " + err.getDefaultMessage())
				.reduce((a, b) -> a + "; " + b)
				.orElse("Error de validación");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(message));
	}
}
