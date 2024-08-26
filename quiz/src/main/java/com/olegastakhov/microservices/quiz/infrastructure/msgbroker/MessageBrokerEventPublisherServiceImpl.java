package com.olegastakhov.microservices.quiz.infrastructure.msgbroker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageBrokerEventPublisherServiceImpl {

    @Autowired
    private AmqpTemplate amqpTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void publish(final String topicExchange,
                        final String routingKey,
                        final Object eventObject) {
        final Map eventAsMap = convert(eventObject);
        amqpTemplate.convertAndSend(topicExchange,
                routingKey,
                eventAsMap);
    }

    private Map convert(final Object eventObject) {
        if (eventObject instanceof Map map) {
            return map;
        }
        return objectMapper.convertValue(eventObject, new TypeReference<>() {});
    }
}
