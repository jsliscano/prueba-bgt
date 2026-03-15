package com.example.prueba.domain.model;

import java.math.BigDecimal;

import com.example.prueba.domain.model.enums.NotificationPreference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Client")
public class ClientTest {

	@Nested
	@DisplayName("hasSufficientBalance")
	class HasSufficientBalance {

		@Test
		@DisplayName("returns true when balance equals amount")
		void trueWhenBalanceEqualsAmount() {
			Client client = clientWithBalance("100000");
			assertThat(client.hasSufficientBalance(new BigDecimal("100000"))).isTrue();
		}

		@Test
		@DisplayName("returns true when balance is greater than amount")
		void trueWhenBalanceGreaterThanAmount() {
			Client client = clientWithBalance("150000");
			assertThat(client.hasSufficientBalance(new BigDecimal("100000"))).isTrue();
		}

		@Test
		@DisplayName("returns false when balance is less than amount")
		void falseWhenBalanceLessThanAmount() {
			Client client = clientWithBalance("50000");
			assertThat(client.hasSufficientBalance(new BigDecimal("100000"))).isFalse();
		}

		@Test
		@DisplayName("returns false when amount is null")
		void falseWhenAmountIsNull() {
			Client client = clientWithBalance("100000");
			assertThat(client.hasSufficientBalance(null)).isFalse();
		}

		@Test
		@DisplayName("returns false when balance is null")
		void falseWhenBalanceIsNull() {
			Client client = Client.builder()
					.id("c1")
					.name("Test")
					.amount(null)
					.notificationPreference(NotificationPreference.EMAIL)
					.email("a@b.com")
					.phone("300")
					.build();
			assertThat(client.hasSufficientBalance(new BigDecimal("100"))).isFalse();
		}
	}

	private static Client clientWithBalance(String balance) {
		return Client.builder()
				.id("c1")
				.name("Test")
				.amount(new BigDecimal(balance))
				.notificationPreference(NotificationPreference.EMAIL)
				.email("a@b.com")
				.phone("300")
				.build();
	}
}
