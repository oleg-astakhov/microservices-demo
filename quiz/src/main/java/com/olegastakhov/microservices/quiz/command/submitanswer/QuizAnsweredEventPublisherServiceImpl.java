package com.olegastakhov.microservices.quiz.command.submitanswer;

import com.olegastakhov.microservices.quiz.domain.attempt.QuizAttempt;
import com.olegastakhov.microservices.quiz.infrastructure.msgbroker.MessageBrokerEventPublisherServiceImpl;
import com.olegastakhov.microservices.quiz.infrastructure.validation.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class QuizAnsweredEventPublisherServiceImpl {
    @Autowired
    MessageBrokerEventPublisherServiceImpl eventPublisher;
    @Value("${amqp.exchange.attempts}")
    private String quizTopicExchange;

    @CircuitBreaker(name = "messageBroker", fallbackMethod = "fallbackMethod")
    public void quizAnswered(final QuizAttempt quizAttempt) {
        final QuizAttemptEvent event = buildEvent(quizAttempt);
        final String routingKey = "attempt." + (event.correct() ? "correct" : "wrong");
        eventPublisher.publish(quizTopicExchange, routingKey, event);
    }

    private void fallbackMethod(CallNotPermittedException ex) {
        throw new ServiceUnavailableException("Message Broker is not available");
    }

    private QuizAttemptEvent buildEvent(QuizAttempt quizAttempt) {
        Assert.notNull(quizAttempt.getReferenceId(), "referenceId is null when not expected");
        Assert.notNull(quizAttempt.getUser(), "user is null when not expected");
        Assert.hasLength(quizAttempt.getUser().getReferenceId(), "user.referenceId is blank when not expected");

        return new QuizAttemptEvent(quizAttempt.getReferenceId(), quizAttempt.isCorrect(), quizAttempt.getUser().getReferenceId());
    }
}
