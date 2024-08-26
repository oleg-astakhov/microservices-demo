package com.olegastakhov.microservices.quiz.command.getnextquestion;

import com.olegastakhov.microservices.quiz.common.dto.ResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
public class QuizNextQuestionController {

    @Autowired
    private GetNextQuestionServiceImpl getNextQuestionService;

    @GetMapping("/quiz/next-question")
    public Mono<ResultDTO<QuizDTO>> getNextQuestion() {
        return getNextQuestionService.getNextQuestion();
    }
}
