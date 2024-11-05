package com.olegastakhov.microservices.gamification.service.quizattemptevent;

import io.micrometer.common.util.StringUtils;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class QuizAttemptEventListenerServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(QuizAttemptEventListenerServiceImpl.class);

    @Autowired
    QuizAttemptEventHandlerServiceImpl quizAttemptEventProcessorService;
    @Autowired
    Tracer tracer;

    @RabbitListener(queues = "${amqp.queue.gamification}")
    void handleQuizAnswered(QuizAttemptEvent event,
                            @Header("traceId") String traceId,
                            @Header("spanId") String spanId) {
        final Span newSpan = getNextSpan(traceId, spanId);

        try (Tracer.SpanInScope ws = tracer.withSpan(newSpan)) {
            log.info("Quiz answered event received with attempt id: {}", event.attemptId());
            quizAttemptEventProcessorService.processEvent(event);
        } catch (final Exception e) {
            log.error("Error when trying to process QuizAttemptEvent", e);
            throw new AmqpRejectAndDontRequeueException(e);
        } finally {
            newSpan.end();
        }
    }

    private Span getNextSpan(String traceId,
                             String spanId) {
        final String spanName = "handleQuizAnswered";
        if (StringUtils.isBlank(traceId)) {
            return tracer.nextSpan().name(spanName);
        }
        TraceContext traceContext = tracer.traceContextBuilder()
                .traceId(traceId)
                .spanId(spanId)
                .sampled(true)
                .build();

        return tracer
                .spanBuilder()
                .setParent(traceContext)
                .name(spanName)
                .start();
    }
}
