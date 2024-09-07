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
        Map quizData = QuizHelper.getNextQuestion(params)

        Set<String> uniqueAnswers = new HashSet<>()

        then: "check returned data - all properties must be initialized"

        null == quizData.message
        quizData.questionId
        quizData.questionItemId
        quizData.question
        VALID_QUESTION_IDS.contains(quizData.questionId)
        !VALID_QUESTION_IDS.contains(quizData.question)
        4 == quizData.options.size()
        with (new AtomicInteger(64)) {rollingLetter -> // 65 = char 'A', 66 = char 'B', etc.
            (quizData.options as List).stream().forEach {option ->
                assert option.valueForBackend
                assert option.displayValue
                // this line checks that options starts from capital letter A:, then B:, then C:, etc.
                assert option.displayValue.startsWith(String.valueOf((char) rollingLetter.incrementAndGet()))
                assert !uniqueAnswers.contains(option.valueForBackend) // verify no repeating options
                uniqueAnswers.add(option.valueForBackend)
            }
            uniqueAnswers.contains(quizData.correctAnswer)
        }
        quizData.submitAnswerButtonCaption == "Submit"
        quizData.usernameInputPlaceholderCaption == "enter your username"
    }
}
