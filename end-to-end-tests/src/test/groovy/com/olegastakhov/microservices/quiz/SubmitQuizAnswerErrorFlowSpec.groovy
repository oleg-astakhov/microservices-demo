package com.olegastakhov.microservices.quiz

import com.olegastakhov.microservices.AbstractSpecification
import com.olegastakhov.microservices.util.E2EQuery
import com.olegastakhov.microservices.util.Helper
import com.olegastakhov.microservices.util.HttpHelper
import com.olegastakhov.microservices.util.httpresponse.E2EMapResponseBody

class SubmitQuizAnswerErrorFlowSpec extends AbstractSpecification {


    def "username not specified"() {
        given: "quiz question is displayed"
        Map data = QuizHelper.getNextQuestion(params)

        when: "user tries to submit answer without specifying username"

        final Map jsonBody = [
                questionId: data.questionId,
                questionItemId: data.questionItemId,
                answer  : data.options.first().valueForBackend
        ]

        params << [ns_jsonAjax_body: jsonBody]
        params << [ns_navi_path: "/quiz/submit-answer"]
        params << [ns_httpStatus_assertFor200: Boolean.FALSE]

        E2EMapResponseBody submitResponse = HttpHelper.postAsJsonAjax(params)

        then: "expect BAD REQUEST and field username is required"

        submitResponse.statusCode == 400
        submitResponse.responseBody.message == "Field username is required"
    }

    def "answer option not specified"() {
        given: "quiz question is displayed"
        Map data = QuizHelper.getNextQuestion(params)
        final String username = Helper.getRandomUsername()

        when: "user tries to submit answer without selecting any answer"

        final Map jsonBody = [
                questionId: data.questionId,
                questionItemId: data.questionItemId,
                username: username
        ]

        params << [ns_jsonAjax_body: jsonBody]
        params << [ns_navi_path: "/quiz/submit-answer"]
        params << [ns_httpStatus_assertFor200: Boolean.FALSE]

        E2EMapResponseBody submitResponse = HttpHelper.postAsJsonAjax(params)

        then: "expect BAD REQUEST and field answer is required"

        submitResponse.statusCode == 400
        submitResponse.responseBody.message == "Field answer is required"
        E2EQuery.AppUser.findRow(username).isEmpty()
    }

    def "frontend dev forgot to initialize questionId"() {
        given: "quiz question is displayed"
        Map data = QuizHelper.getNextQuestion(params)
        final String username = Helper.getRandomUsername()

        when: "user fill all the data but frontend dev did not initialize all fields"

        final Map jsonBody = [
                questionItemId: data.questionItemId,
                answer  : data.options.first().valueForBackend,
                username: username
        ]

        params << [ns_jsonAjax_body: jsonBody]
        params << [ns_navi_path: "/quiz/submit-answer"]
        params << [ns_httpStatus_assertFor200: Boolean.FALSE]

        E2EMapResponseBody submitResponse = HttpHelper.postAsJsonAjax(params)

        then: "expect 400 BAD REQUEST and polite sorry message"

        submitResponse.statusCode == 400
        submitResponse.responseBody.message == Helper.GENERIC_INTERNAL_ERROR_MESSAGE_TO_USER
        E2EQuery.AppUser.findRow(username).isEmpty()
    }

    def "frontend dev forgot to initialize questionItemId"() {
        given: "quiz question is displayed"
        Map data = QuizHelper.getNextQuestion(params)
        final String username = Helper.getRandomUsername()

        when: "user fill all the data but frontend dev did not initialize all fields"

        final Map jsonBody = [
                questionId: data.questionId,
                answer  : data.options.first().valueForBackend,
                username: username
        ]

        params << [ns_jsonAjax_body: jsonBody]
        params << [ns_navi_path: "/quiz/submit-answer"]
        params << [ns_httpStatus_assertFor200: Boolean.FALSE]

        E2EMapResponseBody submitResponse = HttpHelper.postAsJsonAjax(params)

        then: "expect 400 BAD REQUEST and polite sorry message"

        submitResponse.statusCode == 400
        submitResponse.responseBody.message == Helper.GENERIC_INTERNAL_ERROR_MESSAGE_TO_USER
        E2EQuery.AppUser.findRow(username).isEmpty()
    }
}
