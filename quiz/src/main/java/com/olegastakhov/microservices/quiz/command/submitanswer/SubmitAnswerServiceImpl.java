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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class SubmitAnswerServiceImpl {

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

    private static final Pattern USERNAME_PATTERN =Pattern.compile("^[0-9a-z\\-]{3,}$");


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
        return new ResultDTO<>(submit(input));
    }

    private SubmitAnswerResultDTO submit(SubmitAnswerData input) {
        if (!USERNAME_PATTERN.matcher(input.getUsername()).find()) {
            throw new ServiceValidationException("users.error.usernameIsInvalid", input.getUsername());
        }

        final QuestionGenerator questionGenerator = getQuestionGenerator(input.getQuestionId());
        final String correctAnswer = questionGenerator.getCorrectAnswer(input.getQuestionItemId());
        final boolean correct = correctAnswer.equals(input.getAnswer());

        final QuizAttempt quizAttempt = new QuizAttempt();
        quizAttempt.setReferenceId(UUID.randomUUID().toString());
        quizAttempt.setUser(userService.findOrCreate(input.getUsername()));
        quizAttempt.setCorrect(correct);
        quizAttempt.setCorrectAnswer(correctAnswer);
        quizAttempt.setUserAnswer(input.getAnswer());
        quizAttempt.setQuestionId(input.getQuestionId());
        quizAttempt.setQuestionItemId(input.getQuestionItemId());
        quizAttempt.setQuestion(questionGenerator.getQuestion(input.getQuestionItemId()));
        quizAttempt.setDateCreated(ZonedDateTime.now());
        quizAttemptRepository.save(quizAttempt);
        final SubmitAnswerResultDTO result = new SubmitAnswerResultDTO();
        result.setCorrect(correct);
        result.setNextQuestionButtonCaption(localization.getLocalizedMessage("quiz.nextQuestionButtonCaption"));
        if (correct) {
            result.setMotivationMessage(localization.getLocalizedMessage("submitAnswer.motivationMessage.correct"));
        } else {
            result.setMotivationMessage(localization.getLocalizedMessage("submitAnswer.motivationMessage.wrong", correctAnswer));
        }

        eventPublisher.quizAnswered(quizAttempt);

        return result;
    }


    private QuestionGenerator getQuestionGenerator(final String questionId) {
        return questionGenerators.stream()
                .filter(it -> it.getId().equals(questionId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(String.format("No handler has been found for question id [%s]", questionId)));
    }
}
