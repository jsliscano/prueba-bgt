package com.example.prueba.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import com.twilio.Twilio;

@Slf4j
@Configuration
public class TwilioConfig {

	@Value("${twilio.account-sid:}")
	private String accountSid;

	@Value("${twilio.auth-token:}")
	private String authToken;

	@PostConstruct
	public void initTwilio() {
		if (accountSid != null && !accountSid.isBlank() && authToken != null && !authToken.isBlank()) {
			Twilio.init(accountSid, authToken);
			log.info("Twilio inicializado correctamente para envío de SMS");
		}
	}
}
