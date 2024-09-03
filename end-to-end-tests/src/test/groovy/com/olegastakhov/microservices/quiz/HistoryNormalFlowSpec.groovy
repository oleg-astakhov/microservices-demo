package com.olegastakhov.microservices.quiz

import com.olegastakhov.microservices.AbstractSpecification
import com.olegastakhov.microservices.util.Helper

class HistoryNormalFlowSpec extends AbstractSpecification {
    def "user submits correct answer - must reflect it in the history endpoint"() {
        when: "user submits correct answer"
        QuizHelper.submitCorrectAnswer(params)
        Map correctQuestionData = Helper.extractCurrentNonNullProperty(Map, "ns_quiz_quizData", params)

        and: "retrieve personal stats"
        Map historyData = QuizHelper.getHistory(params)

        then: "attempt must be returned"
        1 == historyData.stats.size()

        historyData.stats.get(0).question == correctQuestionData.question
        historyData.stats.get(0).correct

        and: "header title must be provided"
        historyData.headerText == "History"
    }

    def "user submits wrong answer - must reflect it in the history endpoint"() {
        when: "user submits correct answer"
        QuizHelper.submitWrongAnswer(params)
        Map wrongQuestionData = Helper.extractCurrentNonNullProperty(Map, "ns_quiz_quizData", params)

        and: "retrieve personal stats"
        Map historyData = QuizHelper.getHistory(params)

        then: "attempt must be returned"
        1 == historyData.stats.size()

        historyData.stats.get(0).question == wrongQuestionData.question
        !historyData.stats.get(0).correct

        and: "header title must be provided"
        historyData.headerText == "History"
    }

    def "user submits correct and wrong answer - must return history of both"() {
        when: "user submits correct answer"
        QuizHelper.submitCorrectAnswer(params)
        Map correctQuestionData = Helper.extractCurrentNonNullProperty(Map, "ns_quiz_quizData", params)

        and: "user submits wrong answer"
        QuizHelper.submitWrongAnswer(params)
        Map wrongQuestionData = Helper.extractCurrentNonNullProperty(Map, "ns_quiz_quizData", params)

        and: "retrieve personal stats"
        Map historyData = QuizHelper.getHistory(params)

        then: "both attempts must be returned in DESC order"
        2 == historyData.stats.size()

        historyData.stats.get(0).question == wrongQuestionData.question
        !historyData.stats.get(0).correct

        historyData.stats.get(1).question == correctQuestionData.question
        historyData.stats.get(1).correct
    }

    def "user submits 2 correct answers in a row - must return history of both"() {
        when: "user submits 2 correct answers in a row"
        QuizHelper.submitCorrectAnswer(params)
        Map correctQuestionData1 = Helper.extractCurrentNonNullProperty(Map, "ns_quiz_quizData", params)

        QuizHelper.submitCorrectAnswer(params)
        Map correctQuestionData2 = Helper.extractCurrentNonNullProperty(Map, "ns_quiz_quizData", params)

        and: "retrieve personal stats"
        Map historyData = QuizHelper.getHistory(params)

        then: "both attempts must be returned in DESC order"
        2 == historyData.stats.size()

        historyData.stats.get(0).question == correctQuestionData2.question
        historyData.stats.get(0).correct

        historyData.stats.get(1).question == correctQuestionData1.question
        historyData.stats.get(1).correct
    }

    def "user submits 2 wrong answers in a row - must return history of both"() {
        when: "user submits 2 wrong answers in a row"
        QuizHelper.submitWrongAnswer(params)
        Map wrongQuestionData1 = Helper.extractCurrentNonNullProperty(Map, "ns_quiz_quizData", params)

        QuizHelper.submitWrongAnswer(params)
        Map wrongQuestionData2 = Helper.extractCurrentNonNullProperty(Map, "ns_quiz_quizData", params)

        and: "retrieve personal stats"
        Map historyData = QuizHelper.getHistory(params)

        then: "both attempts must be returned in DESC order"
        2 == historyData.stats.size()

        historyData.stats.get(0).question == wrongQuestionData2.question
        !historyData.stats.get(0).correct

        historyData.stats.get(1).question == wrongQuestionData1.question
        !historyData.stats.get(1).correct
    }
}
