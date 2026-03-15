package com.example.prueba.infrastructure.entrypoint.exception;


public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String message) {
		super(message);
	}
}
