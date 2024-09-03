package com.olegastakhov.microservices.quiz.command.getnextquestion;

import com.olegastakhov.microservices.quiz.infrastructure.EnvironmentServiceImpl;
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
    EnvironmentServiceImpl environmentService;

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

    protected QuizDTO map(QuizData quizData) {
        final QuizDTO result = new QuizDTO();

        result.setQuestionId(quizData.getQuestionId())
                .setQuestionItemId(quizData.getQuestionItemId())
                .setQuestion(quizData.getQuestion())
                .setOptions(mapOptions(quizData.getOptions()))
                .setSubmitAnswerButtonCaption(localization.getLocalizedMessage("quiz.submitAnswerButtonCaption"))
                .setUsernameInputPlaceholderCaption(localization.getLocalizedMessage("quiz.usernameInputPlaceholderCaption"));

        if (environmentService.isCurrentEnvironmentEndToEndTests()) {
            result.setCorrectAnswer(quizData.getCorrectAnswer());
        }

        return result;
    }

    public List<QuizOptionDTO> mapOptions(final List<String> options) {
        final AtomicInteger rollingLetter = new AtomicInteger(64); // 65 = char 'A', 66 = char 'B', etc.

        return options.stream()
                .map(it -> new QuizOptionDTO(it,
                        localization.getLocalizedMessage(
                                "quiz.option",
                                String.valueOf((char) rollingLetter.incrementAndGet()), it)
                )).toList();
    }
}
