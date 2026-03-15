package com.example.prueba.infrastructure.drivenadapter.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.prueba.domain.model.enums.NotificationPreference;
import com.example.prueba.domain.port.NotificatorPort;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
public class NotificadorAdapter implements NotificatorPort {

	private static final String SUBSCRIPTION_MESSAGE = "Su suscripción al fondo %s ha sido realizada correctamente. Gracias por confiar en nosotros.";

	@Autowired(required = false)
	private JavaMailSender mailSender;

	@Value("${app.notificacion.email-envios:false}")
	private boolean sendEmail;

	@Value("${twilio.account-sid:}")
	private String twilioAccountSid;

	@Value("${twilio.auth-token:}")
	private String twilioAuthToken;

	@Value("${twilio.phone-from:}")
	private String twilioPhoneFrom;

	public NotificadorAdapter() {
	}

	@Override
	public Mono<Void> notifySubscription(NotificationPreference preference, String destination, String fundName) {
		if (preference == NotificationPreference.EMAIL) {
			return sendEmail(destination, fundName);
		} else {
			return sendSms(destination, fundName);
		}
	}

	private Mono<Void> sendEmail(String destination, String fundName) {
		if (!sendEmail || mailSender == null || destination == null || destination.isBlank()) {
			log.info("EMAIL no enviado (deshabilitado o sin destino): {} - fondo {}", destination, fundName);
			return Mono.empty();
		}
		return Mono.fromRunnable(() -> {
			try {
				SimpleMailMessage msg = new SimpleMailMessage();
				msg.setTo(destination.trim());
				msg.setSubject("Suscripción al fondo " + fundName);
				msg.setText(String.format(SUBSCRIPTION_MESSAGE, fundName));
				mailSender.send(msg);
				log.info("Correo enviado a {} para fondo {}", destination, fundName);
			} catch (Exception e) {
				log.error("Error enviando correo a {}: {}", destination, e.getMessage());
			}
		}).subscribeOn(Schedulers.boundedElastic()).then();
	}

	private Mono<Void> sendSms(String phone, String fundName) {
		if (phone == null || phone.isBlank() || twilioAccountSid == null || twilioAccountSid.isBlank()
				|| twilioAuthToken == null || twilioAuthToken.isBlank() || twilioPhoneFrom == null || twilioPhoneFrom.isBlank()) {
			log.info("SMS no enviado (Twilio no configurado o sin teléfono): {} - fondo {}", phone, fundName);
			return Mono.empty();
		}
		return Mono.fromRunnable(() -> {
			try {
				String toE164 = formatPhoneE164(phone.trim());
				String body = String.format(SUBSCRIPTION_MESSAGE, fundName);
				com.twilio.rest.api.v2010.account.Message.creator(
						new com.twilio.type.PhoneNumber(toE164),
						new com.twilio.type.PhoneNumber(twilioPhoneFrom.trim()),
						body
				).create();
				log.info("SMS enviado a {} para fondo {}", toE164, fundName);
			} catch (Exception e) {
				log.error("Error enviando SMS a {}: {}", phone, e.getMessage());
			}
		}).subscribeOn(Schedulers.boundedElastic()).then();
	}

	private String formatPhoneE164(String phone) {
		String soloNumeros = phone.replaceAll("\\D", "");
		if (soloNumeros.length() == 10 && soloNumeros.startsWith("3")) {
			return "+57" + soloNumeros;
		}
		if (!phone.startsWith("+")) {
			return "+" + soloNumeros;
		}
		return phone;
	}
}
