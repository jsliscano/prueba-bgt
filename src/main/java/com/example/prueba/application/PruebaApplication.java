package com.example.prueba.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.prueba")
@EnableReactiveMongoRepositories(basePackages = "com.example.prueba.infrastructure.drivenadapter.persistence")

public class PruebaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PruebaApplication.class, args);
	}

}
