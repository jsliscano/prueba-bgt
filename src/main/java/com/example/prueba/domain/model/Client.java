package com.example.prueba.domain.model;

import java.math.BigDecimal;

import com.example.prueba.domain.model.enums.NotificationPreference;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Client {
	String id;
	String name;
	BigDecimal amount;
	NotificationPreference notificationPreference;
	String email;
	String phone;

	public boolean hasSufficientBalance(BigDecimal amount) {
		return amount != null && this.amount != null && this.amount.compareTo(amount) >= 0;
	}
}

