package com.olegastakhov.microservices.quiz.command.submitanswer;


import com.olegastakhov.microservices.quiz.domain.attempt.QuizAttempt;
import com.olegastakhov.microservices.quiz.infrastructure.validation.MandatoryFieldNotInitializedOnClientException;
import com.olegastakhov.microservices.quiz.infrastructure.validation.ServiceValidationException;
import com.olegastakhov.microservices.quiz.service.quizes.common.api.QuestionGenerator;
import com.olegastakhov.microservices.quiz.domain.attempt.QuizAttemptRepository;
import com.olegastakhov.microservices.quiz.common.dto.ResultDTO;
import com.olegastakhov.microservices.quiz.service.UserServiceImpl;
import com.olegastakhov.microservices.quiz.infrastructure.localization.LocalizationServiceImpl;
import io.micrometer.common.util.StringUtils;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.annotation.Observed;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class SubmitAnswerServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(SubmitAnswerServiceImpl.class);

    @Autowired
    private Collection<QuestionGenerator> questionGenerators;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private QuizAttemptRepository quizAttemptRepository;
    @Autowired
    private LocalizationServiceImpl localization;
    @Autowired
    private QuizAnsweredEventPublisherServiceImpl eventPublisher;
    @Autowired
    private Tracer tracer;
    @Autowired
    private MeterRegistry meterRegistry;

    private static final Pattern USERNAME_PATTERN =Pattern.compile("^[0-9a-z\\-]{3,}$");


    @Observed(name = "submit_answer", // metric name
            contextualName = "submitting_answer", // span name
            lowCardinalityKeyValues = {"type", "quiz"}) // tag for both metric & span
    @Transactional
    public ResultDTO<SubmitAnswerResultDTO> submitAnswer(SubmitAnswerData input) {
        if (StringUtils.isBlank(input.getQuestionId())) {
            // passed by programmer from UI, user is not at fault
            throw new MandatoryFieldNotInitializedOnClientException("questionId is null when not expected");
        }
        if (StringUtils.isBlank(input.getQuestionItemId())) {
            // passed by programmer from UI, user is not at fault
            throw new MandatoryFieldNotInitializedOnClientException("questionItemId is null when not expected");
        }
        Span newSpan = this.tracer.nextSpan().name("submitQuizAnswer");
        try (Tracer.SpanInScope ws = tracer.withSpan(newSpan.start())) {
            return new ResultDTO<>(submit(input));
        } finally {
            newSpan.end();
        }
    }

    private SubmitAnswerResultDTO submit(SubmitAnswerData input) {
        if (!USERNAME_PATTERN.matcher(input.getUsername()).find()) {
            throw new ServiceValidationException("users.error.usernameIsInvalid", input.getUsername());
        }

        final QuestionGenerator questionGenerator = getQuestionGenerator(input.getQuestionId());
        final String correctAnswer = questionGenerator.getCorrectAnswer(input.getQuestionItemId());
        final boolean correct = correctAnswer.equals(input.getAnswer());

        final QuizAttempt quizAttempt = new QuizAttempt()
                .setReferenceId(UUID.randomUUID().toString())
                .setUser(userService.findOrCreate(input.getUsername()))
                .setCorrect(correct)
                .setCorrectAnswer(correctAnswer)
                .setUserAnswer(input.getAnswer())
                .setQuestionId(input.getQuestionId())
                .setQuestionItemId(input.getQuestionItemId())
                .setQuestion(questionGenerator.getQuestion(input.getQuestionItemId()))
                .setDateCreated(ZonedDateTime.now());

        quizAttemptRepository.save(quizAttempt);
        final SubmitAnswerResultDTO result = new SubmitAnswerResultDTO();
        result.setCorrect(correct);
        result.setNextQuestionButtonCaption(localization.getLocalizedMessage("quiz.nextQuestionButtonCaption"));
        if (correct) {
            result.setMotivationMessage(localization.getLocalizedMessage("submitAnswer.motivationMessage.correct"));
        } else {
            result.setMotivationMessage(localization.getLocalizedMessage("submitAnswer.motivationMessage.wrong", correctAnswer));
        }

        if (correct) {
            /**
             * PoC custom metrics
             */
            meterRegistry.counter("quiz_attempt_correct").increment();
        } else {
            meterRegistry.counter("quiz_attempt_wrong").increment();
        }


        /**
         * PoC custom tracing spans
         */
        Span newSpan = tracer.nextSpan().name("publishQuizAttemptEvent");
        try (Tracer.SpanInScope ws = tracer.withSpan(newSpan.start())) {
            eventPublisher.quizAnswered(quizAttempt);
        } finally {
            newSpan.end();
        }

        return result;
    }


    private QuestionGenerator getQuestionGenerator(final String questionId) {
        return questionGenerators.stream()
                .filter(it -> it.getId().equals(questionId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(String.format("No handler has been found for question id [%s]", questionId)));
    }
}
