package com.olegastakhov.microservices.quiz.command.getnextquestion

import com.olegastakhov.microservices.quiz.infrastructure.EnvironmentServiceImpl
import com.olegastakhov.microservices.quiz.infrastructure.localization.LocalizationServiceImpl
import com.olegastakhov.microservices.quiz.service.quizes.common.api.QuizData
import spock.lang.Specification

class GetNextQuestionServiceImplSpec extends Specification {
    def "mapOptions"() {
        setup:
        GetNextQuestionServiceImpl beanUnderTesting = new GetNextQuestionServiceImpl()
        beanUnderTesting.localization = Spy(LocalizationServiceImpl)
        options.size() * beanUnderTesting.localization.getLocalizedMessage("quiz.option", _) >> {
            String code, String... args ->
                return args[0] + ": " + args[1]
        }

        when:
        List<QuizOptionDTO> result = beanUnderTesting.mapOptions(options)

        then:
        result.stream().map { it.displayValue() }.toList().join("|") == displayValue
        result.stream().map { it.valueForBackend() }.toList().join("|") == valueForBackend

        where:
        options                               || displayValue                    || valueForBackend
        List.of("o1")                         || "A: o1"                         || "o1"
        List.of("o1", "o2", "o3")             || "A: o1|B: o2|C: o3"             || "o1|o2|o3"
        List.of("o1", "o2", "o3", "o4", "o5") || "A: o1|B: o2|C: o3|D: o4|E: o5" || "o1|o2|o3|o4|o5"
    }

    def "map - verify mappings"() {
        setup:
        GetNextQuestionServiceImpl beanUnderTesting = setUpBeanForTestingMapMethod(false)

        when:
        QuizDTO result = beanUnderTesting.map(
                new QuizData()
                        .setQuestion("question")
                        .setQuestionId("question_id")
                        .setQuestionItemId("question_item_id")
                        .setCorrectAnswer("correct_answer")
        )

        then:
        result.getQuestion() == "question"
        result.getQuestionId() == "question_id"
        result.getQuestionItemId() == "question_item_id"
        result.getSubmitAnswerButtonCaption() == "submitAnswerButtonCaption"
        result.getUsernameInputPlaceholderCaption() == "usernameInputPlaceholderCaption"
    }

    def "map - verify that setCorrectAnswer is called only for test environment"() {
        setup:
        GetNextQuestionServiceImpl beanUnderTesting = setUpBeanForTestingMapMethod(true)

        when:
        QuizDTO result = beanUnderTesting.map(
                new QuizData()
                        .setCorrectAnswer("correct_answer")
        )

        then: "correct answer must be initialized only for end-to-end tests"
        result.getCorrectAnswer() == "correct_answer"
    }

    def "map - verify that setCorrectAnswer is not called for non-test environment"() {
        setup:
        GetNextQuestionServiceImpl beanUnderTesting = setUpBeanForTestingMapMethod(false)

        when:
        QuizDTO result = beanUnderTesting.map(
                new QuizData()
                        .setCorrectAnswer("correct_answer")
        )

        then: "correct answer must not be initialized for any other environment except end-to-end tests"
        result.getCorrectAnswer() == null
    }

    private GetNextQuestionServiceImpl setUpBeanForTestingMapMethod(final endToEndTests) {
        GetNextQuestionServiceImpl beanUnderTesting = Spy(GetNextQuestionServiceImpl)
        beanUnderTesting.localization = Spy(LocalizationServiceImpl)
        beanUnderTesting.environmentService = Spy(EnvironmentServiceImpl)
        1 * beanUnderTesting.localization.getLocalizedMessage("quiz.submitAnswerButtonCaption") >> "submitAnswerButtonCaption"
        1 * beanUnderTesting.localization.getLocalizedMessage("quiz.usernameInputPlaceholderCaption") >> "usernameInputPlaceholderCaption"
        1 * beanUnderTesting.mapOptions(_) >> Collections.emptyList()
        1 * beanUnderTesting.environmentService.isCurrentEnvironmentEndToEndTests() >> endToEndTests
        return beanUnderTesting
    }
}
