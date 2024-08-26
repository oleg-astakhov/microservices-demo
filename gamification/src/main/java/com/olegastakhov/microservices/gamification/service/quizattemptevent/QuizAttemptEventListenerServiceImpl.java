package com.olegastakhov.microservices.gamification.service.quizattemptevent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuizAttemptEventListenerServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(QuizAttemptEventListenerServiceImpl.class);

    @Autowired
    QuizAttemptEventHandlerServiceImpl quizAttemptEventProcessorService;

    @RabbitListener(queues = "${amqp.queue.gamification}")
    void handleQuizAnswered(QuizAttemptEvent event) {
        log.info("Quiz answered event received with attempt id: {}", event.attemptId());
        try {
            quizAttemptEventProcessorService.processEvent(event);
        } catch (final Exception e) {
            log.error("Error when trying to process QuizAttemptEvent", e);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}
