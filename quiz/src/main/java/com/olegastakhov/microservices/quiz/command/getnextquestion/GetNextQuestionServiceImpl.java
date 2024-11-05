package com.olegastakhov.microservices.quiz.command.getnextquestion;

import com.olegastakhov.microservices.quiz.infrastructure.EnvironmentServiceImpl;
import com.olegastakhov.microservices.quiz.service.quizes.common.api.QuestionGenerator;
import com.olegastakhov.microservices.quiz.service.quizes.common.api.QuizData;
import com.olegastakhov.microservices.quiz.common.dto.ResultDTO;
import com.olegastakhov.microservices.quiz.infrastructure.localization.LocalizationServiceImpl;
import io.micrometer.core.annotation.Timed;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class GetNextQuestionServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(GetNextQuestionServiceImpl.class);

    @Autowired
    private List<QuestionGenerator> questionGenerators;
    @Autowired
    LocalizationServiceImpl localization;
    @Autowired
    EnvironmentServiceImpl environmentService;

    /**
     * PoC AOP-driven vendor-neutral metrics
     */
    @Timed(value = "get_next_question_time", description = "Time taken to generate next question")
    /**
     * PoC AOP-driven vendor-neutral metrics and traces
     */
    @Observed(name = "next_question", // metric name
            contextualName = "getting_next_question", // span name
            lowCardinalityKeyValues = {"type", "quiz"}) // tag for both metric & span
    public ResultDTO<QuizDTO> getNextQuestion() {
        return new ResultDTO<>(getQuizDTO());
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
