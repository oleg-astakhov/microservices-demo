package com.olegastakhov.microservices.quiz

import com.olegastakhov.microservices.AbstractSpecification
import com.olegastakhov.microservices.util.E2EQuery
import com.olegastakhov.microservices.util.Helper
import groovy.sql.GroovyRowResult

import java.util.stream.Collectors

class GetUsersNormalFlowSpec extends AbstractSpecification {
    def "3 users submits answers - verify endpoint that returns user data"() {
        given: "user1 submits correct answer"
        final String username1 =  Helper.getRandomUsername()
        params << [ns_quiz_username : username1]
        QuizHelper.submitCorrectAnswer(params)

        and: "user2 submits wrong answer"
        final String username2 =  Helper.getRandomUsername()
        params << [ns_quiz_username : username2]
        QuizHelper.submitWrongAnswer(params)

        and: "user3 submits correct answer"
        final String username3 =  Helper.getRandomUsername()
        params << [ns_quiz_username : username3]
        QuizHelper.submitCorrectAnswer(params)

        when: "get all 3 user records directly from db"
        final List<String> usernames = Arrays.asList(username1, username2, username3)
        List<GroovyRowResult> userRows = E2EQuery.AppUser.list(usernames)

        then: "must be 3 users"
        userRows.size() == 3

        when: "request user data for 3 users"
        Map<String, String> usernameToRef = userRows.stream()
                .collect(Collectors.toMap(key -> key.username, value -> value.reference_id));

        Map userData = QuizHelper.getUsers(params, usernameToRef.values())
        Map refToUser = userData.refToUser

        then: "expect to receive info for all 3 users"
        refToUser.size() == 3

        refToUser.get(usernameToRef.get(username1)).username == username1
        refToUser.get(usernameToRef.get(username2)).username == username2
        refToUser.get(usernameToRef.get(username3)).username == username3
    }
}
