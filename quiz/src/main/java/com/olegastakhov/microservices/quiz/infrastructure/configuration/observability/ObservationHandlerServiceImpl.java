package com.olegastakhov.microservices.quiz.infrastructure.configuration.observability;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.observation.ServerRequestObservationContext;
import org.springframework.stereotype.Component;
import io.micrometer.common.KeyValue;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * PoC AOP-driven vendor-neutral observation custom actions
 */

@Component
public class ObservationHandlerServiceImpl implements ObservationHandler<Observation.Context> {
    private static final Logger log = LoggerFactory.getLogger(ObservationHandlerServiceImpl.class);

    private static final List<Class> CONTEXTS_TO_FILTER = List.of(ServerRequestObservationContext.class);

    @Override
    public void onStart(Observation.Context context) {
        final List<String> lowCardinalityValues = getLowCardinalityValues(context);
        log.info("Before running the observation for context [{}], tags {}", context.getName(), lowCardinalityValues);
    }

    @Override
    public void onStop(Observation.Context context) {
        final List<String> lowCardinalityValues = getLowCardinalityValues(context);
        log.info("After running the observation for context [{}], tags [{}]", context.getName(), lowCardinalityValues);
    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        return CONTEXTS_TO_FILTER.stream()
                .filter(it -> context.getClass().isAssignableFrom(it))
                .findAny()
                .isEmpty();
    }

    private List<String> getLowCardinalityValues(Observation.Context context) {
        return StreamSupport.stream(context.getLowCardinalityKeyValues().spliterator(), false)
                .map(KeyValue::getValue)
                .toList();
    }
}
