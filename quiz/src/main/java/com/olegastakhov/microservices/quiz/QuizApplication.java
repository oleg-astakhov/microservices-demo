package com.olegastakhov.microservices.quiz;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;

@SpringBootApplication
public class QuizApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuizApplication.class, args);
	}

	@PostConstruct
	public void configureReactor() {
		/**
		 * Initially added this for Micrometer Tracing
		 * so that %X{traceId} and %X{spanId}
		 * would output non-blank values.
		 */
		Hooks.enableAutomaticContextPropagation();
	}
}
