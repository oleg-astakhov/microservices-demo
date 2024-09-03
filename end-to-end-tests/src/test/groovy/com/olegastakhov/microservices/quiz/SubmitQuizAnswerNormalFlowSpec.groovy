package com.olegastakhov.microservices.quiz

import com.olegastakhov.microservices.AbstractSpecification
import com.olegastakhov.microservices.util.E2EQuery
import com.olegastakhov.microservices.util.Helper
import com.olegastakhov.microservices.util.httpresponse.E2EMapResponseBody
import groovy.sql.GroovyRowResult

class SubmitQuizAnswerNormalFlowSpec extends AbstractSpecification {
    def "user submits correct answer - verify endpoint return data"() {
        when: "user submits correct answer"
        E2EMapResponseBody submitResponse = QuizHelper.submitCorrectAnswer(params)
        Map body = submitResponse.responseBody.data

        then: "expect to receive a confirmation that user got the question right"
        submitResponse.statusCode == 200
        null == body.message
        body.correct
        body.motivationMessage == "Good job, try next one!"

        when: "try to find user"
        final String username = Helper.extractCurrentNonBlankStringProperty("ns_quiz_username", params)
        Optional<GroovyRowResult> userData = E2EQuery.AppUser.findRow(username)

        then: "user must be created if it did not previously exist"
        userData.isPresent()
    }

    def "user submits correct answer - attempt must be persisted"() {
        when: "user submits correct answer"
        QuizHelper.submitCorrectAnswer(params)
        Map quizData = Helper.extractCurrentNonNullProperty(Map, "ns_quiz_quizData", params)
        final String username = Helper.extractCurrentNonBlankStringProperty("ns_quiz_username", params)
        String correctAnswer = quizData.correctAnswer
        List<GroovyRowResult> attempts = E2EQuery.QueryAttempt.listDescending(username)

        then: "verify persisted attempt"
        1 == attempts.size()
        with(attempts.first()) {
            it.correct
            it.question_id == quizData.questionId
            it.question_item_id == quizData.questionItemId
            it.question == quizData.question
            it.correct_answer == correctAnswer
            it.user_answer == correctAnswer
        }
    }

    def "user submits wrong answer - verify endpoint return data"() {
        when: "user submits wrong answer"

        E2EMapResponseBody submitResponse = QuizHelper.submitWrongAnswer(params)
        final String username = Helper.extractCurrentNonBlankStringProperty("ns_quiz_username", params)
        Map quizData = Helper.extractCurrentNonNullProperty(Map, "ns_quiz_quizData", params)
        String correctAnswer = quizData.correctAnswer
        Map body = submitResponse.responseBody.data

        then: "expect to receive correct answer and a motivational message"

        submitResponse.statusCode == 200 // from a point of view of HTTP response it is a success
        null == body.message
        !body.correct
        body.motivationMessage == "Correct answer is ${correctAnswer}. That's okay, just keep on training!"
        body.nextQuestionButtonCaption == "Next Question"

        and: "user must be created if it did not previously exist"
        E2EQuery.AppUser.findRow(username).isPresent()
    }

    def "user submits wrong answer - attempt must be persisted"() {
        when: "user submits wrong answer"
        QuizHelper.submitWrongAnswer(params)
        final String username = Helper.extractCurrentNonBlankStringProperty("ns_quiz_username", params)
        Map quizData = Helper.extractCurrentNonNullProperty(Map, "ns_quiz_quizData", params)
        String correctAnswer = quizData.correctAnswer
        String usersWrongAnswer = Helper.extractCurrentNonBlankStringProperty("ns_quiz_userAnswer", params)

        List<GroovyRowResult> attempts = E2EQuery.QueryAttempt.listDescending(username)

        then: "verify persisted attempt"
        1 == attempts.size()
        with(attempts.first()) {
            !it.correct
            it.question_id == quizData.questionId
            it.question_item_id == quizData.questionItemId
            it.question == quizData.question
            it.correct_answer == correctAnswer
            it.user_answer == usersWrongAnswer
        }
    }

    def "user submits correct and wrong answer - must persist both attempts"() {
        when: "user submits correct answer"
        QuizHelper.submitCorrectAnswer(params)
        Map correctQuestionData = Helper.extractCurrentNonNullProperty(Map, "ns_quiz_quizData", params)

        and: "user submits wrong answer"

        QuizHelper.submitWrongAnswer(params)
        Map wrongQuestionData = Helper.extractCurrentNonNullProperty(Map, "ns_quiz_quizData", params)

        String username = Helper.extractCurrentNonBlankStringProperty("ns_quiz_username", params)

        List<GroovyRowResult> attempts = E2EQuery.QueryAttempt.listDescending(username)

        then: "both attempts must be registered"
        2 == attempts.size()

        and: "but only one of them must be flagged as correct"
        // they are sorted in DESC order
        !attempts.get(0).correct
        attempts.get(0).question_id == wrongQuestionData.questionId
        attempts.get(0).question_item_id == wrongQuestionData.questionItemId
        attempts.get(1).correct
        attempts.get(1).question_id == correctQuestionData.questionId
        attempts.get(1).question_item_id == correctQuestionData.questionItemId
    }

    def "user submits 2 correct answers - must persist both attempts"() {
        when: "user submits 2 correct answers"
        QuizHelper.submitCorrectAnswer(params)
        Map correctQuestionData1 = Helper.extractCurrentNonNullProperty(Map, "ns_quiz_quizData", params)
        QuizHelper.submitCorrectAnswer(params)
        Map correctQuestionData2 = Helper.extractCurrentNonNullProperty(Map, "ns_quiz_quizData", params)

        final String username = Helper.extractCurrentNonBlankStringProperty("ns_quiz_username", params)
        List<GroovyRowResult> attempts = E2EQuery.QueryAttempt.listDescending(username)

        then: "both attempts must be registered as correct"
        2 == attempts.size()
        // they are sorted in DESC order
        attempts.get(0).correct
        attempts.get(0).question_id == correctQuestionData2.questionId
        attempts.get(0).question_item_id == correctQuestionData2.questionItemId
        attempts.get(1).correct
        attempts.get(1).question_id == correctQuestionData1.questionId
        attempts.get(1).question_item_id == correctQuestionData1.questionItemId
    }

    def "user submits 2 wrong answers - must persist both attempts"() {
        when: "user submits 2 wrong answers"
        QuizHelper.submitWrongAnswer(params)
        Map wrongQuestionData1 = Helper.extractCurrentNonNullProperty(Map, "ns_quiz_quizData", params)
        QuizHelper.submitWrongAnswer(params)
        Map wrongQuestionData2 = Helper.extractCurrentNonNullProperty(Map, "ns_quiz_quizData", params)

        final String username = Helper.extractCurrentNonBlankStringProperty("ns_quiz_username", params)
        List<GroovyRowResult> attempts = E2EQuery.QueryAttempt.listDescending(username)

        then: "both attempts must be registered as correct"
        2 == attempts.size()
        // they are sorted in DESC order
        !attempts.get(0).correct
        attempts.get(0).question_id == wrongQuestionData2.questionId
        attempts.get(0).question_item_id == wrongQuestionData2.questionItemId
        !attempts.get(1).correct
        attempts.get(1).question_id == wrongQuestionData1.questionId
        attempts.get(1).question_item_id == wrongQuestionData1.questionItemId
    }
}
