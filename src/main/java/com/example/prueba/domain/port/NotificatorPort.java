package com.example.prueba.domain.port;

import com.example.prueba.domain.model.enums.NotificationPreference;
import reactor.core.publisher.Mono;

public interface NotificatorPort {

	Mono<Void> notifySubscription(NotificationPreference preference, String destination, String fundName);
}
