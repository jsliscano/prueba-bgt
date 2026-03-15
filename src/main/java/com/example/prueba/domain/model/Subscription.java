package com.example.prueba.domain.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Subscription {
	String id;
	String clientId;
	Long fondId;
	BigDecimal amountInvolved;
	boolean active;
}
