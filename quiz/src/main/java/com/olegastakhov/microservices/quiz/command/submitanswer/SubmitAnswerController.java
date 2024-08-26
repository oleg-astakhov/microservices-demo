package com.olegastakhov.microservices.quiz.command.submitanswer;

import com.olegastakhov.microservices.quiz.common.dto.ResultDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@RestController
public class SubmitAnswerController {
    @Autowired
    private SubmitAnswerServiceImpl submitAnswerService;
    @Autowired
    @Qualifier("GeneralPurposeVirtualThreadScheduler")
    private Scheduler virtualThreadScheduler;


    @PostMapping("/quiz/submit-answer")
    public Mono<ResultDTO<SubmitAnswerResultDTO>> submitAnswer(@RequestBody @Valid SubmitAnswerData data) {
        return Mono.fromCallable(() -> submitAnswerService.submitAnswer(data))
                .subscribeOn(virtualThreadScheduler);
    }
}
