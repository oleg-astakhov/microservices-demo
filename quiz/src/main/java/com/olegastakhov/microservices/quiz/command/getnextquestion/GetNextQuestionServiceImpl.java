package com.olegastakhov.microservices.quiz.command.getnextquestion;

import com.olegastakhov.microservices.quiz.service.quizes.common.api.QuestionGenerator;
import com.olegastakhov.microservices.quiz.service.quizes.common.api.QuizData;
import com.olegastakhov.microservices.quiz.common.dto.ResultDTO;
import com.olegastakhov.microservices.quiz.infrastructure.localization.LocalizationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class GetNextQuestionServiceImpl {
    @Autowired
    private List<QuestionGenerator> questionGenerators;
    @Autowired
    LocalizationServiceImpl localization;

    @Autowired
    @Qualifier("GeneralPurposeVirtualThreadScheduler")
    private Scheduler virtualThreadScheduler;

    public Mono<ResultDTO<QuizDTO>> getNextQuestion() {
        return Mono.fromCallable(() -> new ResultDTO<>(getQuizDTO()))
                .subscribeOn(virtualThreadScheduler);
    }

    private QuizDTO getQuizDTO() {
        return map(questionGenerators.get(new Random().nextInt(0, questionGenerators.size())).generate());
    }

    private QuizDTO map(QuizData quizData) {
        return new QuizDTO(
                quizData.getQuestionId(),
                quizData.getQuestionItemId(),
                quizData.getQuestion(),
                mapOptions(quizData.getOptions()),
                localization.getLocalizedMessage("quiz.submitAnswerButtonCaption"),
                localization.getLocalizedMessage("quiz.usernameInputPlaceholderCaption")
        );
    }

    public List<QuizOptionDTO> mapOptions(final List<String> options) {
        final AtomicInteger rollingLetter = new AtomicInteger(64); // 64 = starting from capital A

        return options.stream()
                .map(it -> new QuizOptionDTO(it,
                        localization.getLocalizedMessage(
                                "quiz.option",
                                String.valueOf((char) rollingLetter.incrementAndGet()), it)
                )).toList();
    }
}
