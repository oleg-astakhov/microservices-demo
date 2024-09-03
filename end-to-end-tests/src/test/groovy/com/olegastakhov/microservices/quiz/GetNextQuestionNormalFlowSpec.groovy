package com.olegastakhov.microservices.quiz

import com.olegastakhov.microservices.AbstractSpecification

import java.util.concurrent.atomic.AtomicInteger

class GetNextQuestionNormalFlowSpec extends AbstractSpecification {

    private static final Set<String> VALID_QUESTION_IDS = new HashSet<>(
            Arrays.asList(
                    "what-is-the-capital-of-country",
                    "on-which-continent-is-country-located",
                    "what-country-has-capital"
            )
    )

    def "get next question - all properties must be initialized"() {
        when: "call endpoint to get the next question"
        Map data = QuizHelper.getNextQuestion(params)

        Set<String> uniqueAnswers = new HashSet<>()

        then: "check returned data - all properties must be initialized"

        null == data.message
        data.questionId
        data.questionItemId
        data.question
        VALID_QUESTION_IDS.contains(data.questionId)
        !VALID_QUESTION_IDS.contains(data.question)
        4 == data.options.size()
        with (new AtomicInteger(64)) {rollingLetter -> // 65 = char 'A', 66 = char 'B', etc.
            (data.options as List).stream().forEach {option ->
                assert option.valueForBackend
                assert option.displayValue
                // this line checks that options starts from capital letter A:, then B:, then C:, etc.
                assert option.displayValue.startsWith(String.valueOf((char) rollingLetter.incrementAndGet()))
                assert !uniqueAnswers.contains(option.valueForBackend) // verify no repeating options
                uniqueAnswers.add(option.valueForBackend)
            }
        }
        data.submitAnswerButtonCaption == "Submit"
        data.usernameInputPlaceholderCaption == "enter your username"
    }
}
