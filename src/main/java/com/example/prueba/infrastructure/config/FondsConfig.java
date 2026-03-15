package com.example.prueba.infrastructure.config;

import java.math.BigDecimal;
import java.util.List;

import com.example.prueba.domain.model.enums.FundCategory;
import com.example.prueba.domain.model.Fund;
import com.example.prueba.domain.port.*;
import com.example.prueba.domain.usecase.CancelSubscriptionUseCase;
import com.example.prueba.domain.usecase.BalanceUseCase;
import com.example.prueba.domain.usecase.SubscribeFundUseCase;
import com.example.prueba.domain.usecase.TransactionHistoryUseCase;
import com.example.prueba.infrastructure.drivenadapter.persistence.FondDocument;
import com.example.prueba.infrastructure.drivenadapter.persistence.SpringDataFondRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FondsConfig {

	private final SpringDataFondRepository fondRepo;

	@Bean
	public SubscribeFundUseCase subscribeFundUseCase(
			ClientRepository clientRepository,
			FondRepository fondRepository,
			SubscriptionRepository subscriptionRepository,
			TransactionRepository transactionRepository,
			NotificatorPort notificatorPort) {
		return new SubscribeFundUseCase(clientRepository, fondRepository, subscriptionRepository, transactionRepository, notificatorPort);
	}

	@Bean
	public CancelSubscriptionUseCase cancelSubscriptionUseCase(
			ClientRepository clientRepository,
			SubscriptionRepository subscriptionRepository,
			TransactionRepository transactionRepository) {
		return new CancelSubscriptionUseCase(clientRepository, subscriptionRepository, transactionRepository);
	}

	@Bean
	public TransactionHistoryUseCase transactionHistoryUseCase(
			ClientRepository clientRepository,
			TransactionRepository transactionRepository) {
		return new TransactionHistoryUseCase(clientRepository, transactionRepository);
	}

	@Bean
	public BalanceUseCase topUpBalanceUseCase(ClientRepository clientRepository) {
		return new BalanceUseCase(clientRepository);
	}

	@Bean
	public CommandLineRunner fondsDataInitializer() {
		return args -> {
			Long count = fondRepo.count().block();
			if (count != null && count == 0) {
				List<Fund> fonds = List.of(
						Fund.builder().id(1L).name("FPV_BTG_PACTUAL_RECAUDADORA").minimumAmount(new BigDecimal("75000")).category(FundCategory.FPV).build(),
						Fund.builder().id(2L).name("FPV_BTG_PACTUAL_ECOPETROL").minimumAmount(new BigDecimal("125000")).category(FundCategory.FPV).build(),
						Fund.builder().id(3L).name("DEUDAPRIVADA").minimumAmount(new BigDecimal("50000")).category(FundCategory.FIC).build(),
						Fund.builder().id(4L).name("FDO-ACCIONES").minimumAmount(new BigDecimal("250000")).category(FundCategory.FIC).build(),
						Fund.builder().id(5L).name("FPV_BTG_PACTUAL_DINAMICA").minimumAmount(new BigDecimal("100000")).category(FundCategory.FPV).build()
				);
				fonds.forEach(f -> fondRepo.save(FondDocument.from(f)).block());
			}
		};
	}
}
