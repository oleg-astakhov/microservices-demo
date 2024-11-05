package com.olegastakhov.microservices.quiz.command.getnextquestion;

import com.olegastakhov.microservices.quiz.common.dto.ResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;


@RestController
public class QuizNextQuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuizNextQuestionController.class);

    @Autowired
    private GetNextQuestionServiceImpl getNextQuestionService;
    @Autowired
    @Qualifier("GeneralPurposeVirtualThreadScheduler")
    private Scheduler virtualThreadScheduler;

    @GetMapping("/quiz/next-question")
    public Mono<ResultDTO<QuizDTO>> getNextQuestion() {
        return Mono.fromCallable(() -> getNextQuestionService.getNextQuestion())
                .subscribeOn(virtualThreadScheduler);
    }
}
