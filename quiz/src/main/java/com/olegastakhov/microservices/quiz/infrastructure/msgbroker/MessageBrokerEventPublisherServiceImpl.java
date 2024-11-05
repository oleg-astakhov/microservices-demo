package com.olegastakhov.microservices.quiz.infrastructure.msgbroker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageBrokerEventPublisherServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(MessageBrokerEventPublisherServiceImpl.class);

    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private Tracer tracer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void publish(final String topicExchange,
                        final String routingKey,
                        final Object eventObject) {
        final Map eventAsMap = convert(eventObject);

        // Get trace information
        Span span = tracer.currentSpan();
        if (null != span) {
            String traceId = span.context().traceId();
            String spanId = span.context().spanId();
            log.debug("Before publishing: traceId [{}], spanId [{}]", traceId, spanId);
            MessageProperties messageProperties = new MessageProperties();

            final MessagePostProcessor messagePostProcessor = message -> {
                message.getMessageProperties().setHeader("traceId", traceId);
                message.getMessageProperties().setHeader("spanId", spanId);
                return message;
            };
            amqpTemplate.convertAndSend(topicExchange,
                    routingKey,
                    eventAsMap,
                    messagePostProcessor);
        } else {
            amqpTemplate.convertAndSend(topicExchange,
                    routingKey,
                    eventAsMap);
        }
    }

    private Map convert(final Object eventObject) {
        if (eventObject instanceof Map map) {
            return map;
        }
        return objectMapper.convertValue(eventObject, new TypeReference<>() {});
    }
}
