package com.olegastakhov.microservices.gamification;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;

@SpringBootApplication
public class GamificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(GamificationApplication.class, args);
	}

	@PostConstruct
	public void configureReactor() {
		Hooks.enableAutomaticContextPropagation();
	}
}
