package com.olegastakhov.microservices.quiz.command.e2egetlogline;

import com.olegastakhov.microservices.quiz.common.dto.ResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;
import java.util.Map;

@Profile(value = "compose-e2e-test")
@RestController
public class E2EGetLogsController {
    private static final Logger log = LoggerFactory.getLogger(E2EGetLogsController.class);

    @Autowired
    E2EInMemoryAppender e2EInMemoryAppender;
    @Autowired
    @Qualifier("GeneralPurposeVirtualThreadScheduler")
    private Scheduler virtualThreadScheduler;


    @GetMapping("/quiz/logs")
    public Mono<ResultDTO<Map<String, List<String>>>> getLogs() {
        log.info("e2e testing the presence of traceId and spanId");
        List<String> logEntries = e2EInMemoryAppender.getLogEntries();
        return Mono.fromCallable(() -> new ResultDTO<>(Map.of("logs", logEntries)))
                .subscribeOn(virtualThreadScheduler);
    }
}