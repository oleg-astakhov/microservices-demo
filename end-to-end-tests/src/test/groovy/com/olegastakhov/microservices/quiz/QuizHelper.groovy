package com.olegastakhov.microservices.quiz

import com.olegastakhov.microservices.util.HTTPClientFactory
import com.olegastakhov.microservices.util.Helper
import com.olegastakhov.microservices.util.HttpHelper
import com.olegastakhov.microservices.util.HttpOperationsUtil
import com.olegastakhov.microservices.util.httpresponse.E2EMapResponseBody

import java.util.stream.Collectors

class QuizHelper {
    static Map getNextQuestion(final Map<String, Object> params) {
        Helper.putDefaultOrGetActual(params, "ns_ajaxJsonHttpClient_instance", { HTTPClientFactory.getInstance().createPublicRestClient() })
        params << [ns_navi_path: "/quiz/next-question"]
        E2EMapResponseBody result = HttpOperationsUtil.httpGETAsJsonAjax(params)
        params << [ns_quiz_quizData: result.responseBody.data as Map]
        return result.responseBody.data as Map
    }

    static Map getHistory(final Map<String, Object> params) {
        Helper.putDefaultOrGetActual(params, "ns_ajaxJsonHttpClient_instance", { HTTPClientFactory.getInstance().createPublicRestClient() })
        String username = Helper.extractCurrentNonBlankStringProperty("ns_quiz_username", params)
        params << [ns_navi_path: "/quiz/personal-stats?username=" + username]
        E2EMapResponseBody result = HttpOperationsUtil.httpGETAsJsonAjax(params)
        params << [ns_quiz_historyData: result.responseBody.data as Map]
        return result.responseBody.data as Map
    }

    static Map getLeaderboard(final Map<String, Object> params) {
        Helper.putDefaultOrGetActual(params, "ns_ajaxJsonHttpClient_instance", { HTTPClientFactory.getInstance().createPublicRestClient() })
        params << [ns_navi_path: "/leaderboard/list"]
        E2EMapResponseBody result = HttpOperationsUtil.httpGETAsJsonAjax(params)
        params << [ns_quiz_leaderboardData: result.responseBody.data as Map]
        return result.responseBody.data as Map
    }

    static Map getUsers(final Map<String, Object> params, final Collection<String> userRefs) {
        Helper.putDefaultOrGetActual(params, "ns_ajaxJsonHttpClient_instance", { HTTPClientFactory.getInstance().createPublicRestClient() })
        params << [ns_navi_path: "/quiz/users?refs=" + userRefs.stream().collect(Collectors.joining(","))]
        E2EMapResponseBody result = HttpOperationsUtil.httpGETAsJsonAjax(params)
        params << [ns_quiz_usersData: result.responseBody.data as Map]
        return result.responseBody.data as Map
    }

    static Map getLogs(final Map<String, Object> params) {
        Helper.putDefaultOrGetActual(params, "ns_ajaxJsonHttpClient_instance", { HTTPClientFactory.getInstance().createPublicRestClient() })
        params << [ns_navi_path: "/quiz/logs"]
        E2EMapResponseBody result = HttpOperationsUtil.httpGETAsJsonAjax(params)
        params << [ns_quiz_logData: result.responseBody.data as Map]
        return result.responseBody.data as Map
    }

    static String getAnyIncorrectAnswer(List options, String correctAnswer) {
        return options.stream()
                .filter { it.valueForBackend != correctAnswer }
                .findAny()
                .orElseThrow()
    }

    static E2EMapResponseBody submitCorrectAnswer(final Map<String, Object> params) {
        return submitAnswer(true, params)
    }

    static E2EMapResponseBody submitWrongAnswer(final Map<String, Object> params) {
        return submitAnswer(false, params)
    }

    private static E2EMapResponseBody submitAnswer(boolean correct,
                                                   final Map<String, Object> params) {
        final String username = Helper.putDefaultOrGetActual(params, "ns_quiz_username", { Helper.getRandomUsername() })

        Map quizData = getNextQuestion(params)

        final String correctAnswer = quizData.correctAnswer
        final String userAnswer = correct ? correctAnswer : getAnyIncorrectAnswer(quizData.options, correctAnswer)

        params << [ns_jsonAjax_body: [
                questionId    : quizData.questionId,
                questionItemId: quizData.questionItemId,
                answer        : userAnswer,
                username      : username
        ]]
        params << [ns_navi_path: "/quiz/submit-answer"]
        params << [ns_httpStatus_assertFor200: Boolean.TRUE]

        E2EMapResponseBody submitResponse = HttpHelper.postAsJsonAjax(params)

        assert submitResponse.responseBody.data.correct == correct

        params << [ns_quiz_submitResponse: submitResponse]
        params << [ns_quiz_userAnswer: userAnswer]

        return submitResponse
    }
}
