package com.example.prueba.infrastructure.entrypoint.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ResourceNotFoundException")
class ResourceNotFoundExceptionTest {

	@Test
	@DisplayName("message is set correctly")
	void message() {
		ResourceNotFoundException ex = new ResourceNotFoundException("Fondo no encontrado: 1");
		assertThat(ex.getMessage()).isEqualTo("Fondo no encontrado: 1");
	}
}
