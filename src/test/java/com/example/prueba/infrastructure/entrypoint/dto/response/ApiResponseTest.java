package com.example.prueba.infrastructure.entrypoint.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ApiResponse")
public class ApiResponseTest {

	@Nested
	@DisplayName("ok")
	class Ok {
		@Test
		@DisplayName("builds success response with data and message")
		void buildsCorrectly() {
			ApiResponse<String> r = ApiResponse.ok("data", "OK");
			assertThat(r.isSuccess()).isTrue();
			assertThat(r.getMessage()).isEqualTo("OK");
			assertThat(r.getData()).isEqualTo("data");
		}
	}

	@Nested
	@DisplayName("created")
	class Created {
		@Test
		@DisplayName("builds success response for creation")
		void buildsCorrectly() {
			ApiResponse<Integer> r = ApiResponse.created(42, "Creado");
			assertThat(r.isSuccess()).isTrue();
			assertThat(r.getData()).isEqualTo(42);
			assertThat(r.getMessage()).isEqualTo("Creado");
		}
	}

	@Nested
	@DisplayName("error")
	class Error {
		@Test
		@DisplayName("builds error response with message and null data")
		void buildsCorrectly() {
			ApiResponse<Void> r = ApiResponse.error("Error de validación");
			assertThat(r.isSuccess()).isFalse();
			assertThat(r.getMessage()).isEqualTo("Error de validación");
			assertThat(r.getData()).isNull();
		}
	}

	@Nested
	@DisplayName("errorWithData")
	class ErrorWithData {
		@Test
		@DisplayName("builds error response with message and data")
		void buildsCorrectly() {
			InsufficientBalanceData data = new InsufficientBalanceData(null);
			ApiResponse<InsufficientBalanceData> r = ApiResponse.errorWithData("Saldo insuficiente", data);
			assertThat(r.isSuccess()).isFalse();
			assertThat(r.getMessage()).isEqualTo("Saldo insuficiente");
			assertThat(r.getData()).isSameAs(data);
		}
	}
}
