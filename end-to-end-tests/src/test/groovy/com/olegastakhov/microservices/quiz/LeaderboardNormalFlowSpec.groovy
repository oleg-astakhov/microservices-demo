package com.olegastakhov.microservices.quiz

import com.olegastakhov.microservices.AbstractSpecification
import com.olegastakhov.microservices.util.E2EQuery
import com.olegastakhov.microservices.util.Helper
import groovy.sql.GroovyRowResult

/**
 * Note that:
 *
 * Data to Gamification service is being sent in async mode,
 * so we have to wait for sometime until results came through
 * the Message Broker.
 *
 * When we are asserting for PRESENCE of data we simply POLL
 * until success (easy and straightforward).
 *
 * But when we are asserting for ABSENCE of data we don't have
 * much choice other than Thread.sleep(), because there's
 * nothing to poll for. This is not very reliable, but it's
 * better than nothing.
 */

class LeaderboardNormalFlowSpec extends AbstractSpecification {
    def "user submits correct answer - must give a score and add user to the leaderboard"() {
        when: "user submits correct answer"
        QuizHelper.submitCorrectAnswer(params)

        final String username = Helper.extractCurrentNonBlankStringProperty("ns_quiz_username", params)
        GroovyRowResult userRow = E2EQuery.AppUser.getRow(username)

        then:
        Helper.poll {
            List<GroovyRowResult> userScores = E2EQuery.Score.listDescending(userRow.reference_id)
            assert userScores.size() == 1
        }

        with(E2EQuery.Score.getLastRow(userRow.reference_id)) {
            10 == it.value
        }

        with(E2EQuery.Leaderboard.get(userRow.reference_id)) {
            10 == it.total_score
        }
    }

    def "user submits wrong answer - no score must be given and user must not be added to the leaderboard" () {
        when: "user submits wrong answer"
        QuizHelper.submitWrongAnswer(params)
        final String username = Helper.extractCurrentNonBlankStringProperty("ns_quiz_username", params)
        GroovyRowResult userRow = E2EQuery.AppUser.getRow(username)

        Thread.sleep(1000L) // emulate Message Broker wait. Read JavaDoc for this class.

        then: "no score must be given"
        assert E2EQuery.Score.count(userRow.reference_id) == 0L

        and: "user must not be added to the leaderboard"
        assert E2EQuery.Leaderboard.count(userRow.reference_id) == 0L
    }

    def "user submits correct and wrong answer - must give score only for correct answer and add user to the leaderboard"() {
        when: "user submits correct answer"
        QuizHelper.submitCorrectAnswer(params)
        and: "user submits wrong answer"
        QuizHelper.submitWrongAnswer(params)


        String username = Helper.extractCurrentNonBlankStringProperty("ns_quiz_username", params)
        GroovyRowResult userRow = E2EQuery.AppUser.getRow(username)


        then: "expect only only score registered"
        Helper.poll {
            List<GroovyRowResult> userScores = E2EQuery.Score.listDescending(userRow.reference_id)
            assert userScores.size() == 1
        }

        with(E2EQuery.Score.getLastRow(userRow.reference_id)) {
            10 == it.value
        }

        and: "total in the leaderboard must be for one answer only"
        with(E2EQuery.Leaderboard.get(userRow.reference_id)) {
            10 == it.total_score
        }
    }


    def "user submits 2 correct answers - must increment total score"() {
        given: "user submits 2 correct answers"
        QuizHelper.submitCorrectAnswer(params)
        QuizHelper.submitCorrectAnswer(params)
        final String username = Helper.extractCurrentNonBlankStringProperty("ns_quiz_username", params)
        GroovyRowResult userRow = E2EQuery.AppUser.findRow(username).get()

        when: "await for 2 scores to be registered"
        List<GroovyRowResult> userScores = Helper.getByPolling {
            List<GroovyRowResult> userScores = E2EQuery.Score.listDescending(userRow.reference_id)
            assert userScores.size() == 2
            return userScores
        }

        then: "expect 2 scores"
        userScores.get(0).value == 10
        userScores.get(1).value == 10

        and: "expect the leaderboard to reflect 2 successful attempts"
        with(E2EQuery.Leaderboard.get(userRow.reference_id)) {
            it.total_score == 20
        }
    }

    def "user submits 3 answers - correct-wrong-correct - must increment total score for 2 of them"() {
        given: "user submits correct answer"
        QuizHelper.submitCorrectAnswer(params)
        and: "a wrong answer"
        QuizHelper.submitWrongAnswer(params)
        and: "a correct answer again"
        QuizHelper.submitCorrectAnswer(params)

        final String username = Helper.extractCurrentNonBlankStringProperty("ns_quiz_username", params)
        GroovyRowResult userRow = E2EQuery.AppUser.findRow(username).get()

        when: "await for 2 scores to be registered"
        List<GroovyRowResult> userScores = Helper.getByPolling {
            List<GroovyRowResult> userScores = E2EQuery.Score.listDescending(userRow.reference_id)
            assert userScores.size() == 2
            return userScores
        }

        then: "expect 2 scores"
        userScores.get(0).value == 10
        userScores.get(1).value == 10

        and: "expect the leaderboard to reflect 2 successful attempts"
        with(E2EQuery.Leaderboard.get(userRow.reference_id)) {
            it.total_score == 20
        }
    }


    def "user submits a correct answers - verify endpoint data"() {
        when: "user submits correct answer"
        QuizHelper.submitCorrectAnswer(params)

        then: "await the leaderboard endpoint to return non-empty result"
        Helper.poll {
            /**
             * There's little we can do to verify this particular user because:
             * - tests are running in parallel, new users always come in
             *   and we don't know how much scores we need to make to end up
             *   in the leaderboard, except get current max score, and always
             *   submit more correct answers. But this is bad, the test will
             *   become slower and slower, because it will need to create more
             *   and more records.
             * - even if we decide to submit a lot of correct answers, we still
             *   cannot guarantee the order of the leaderboard, because some other
             *   parallel test might be testing something different, which again
             *   shuffles the leaderboard.
             *
             * There is of course a way to leaderboard without any interference.
             * For this, we need to add another column to table leaderboard:
             * correlation_id.
             *
             * So all tests will be writing their own correlation_ids, and db query
             * will always include it as part of the query. That way it will never
             * return data that has been created in another test. If this was a real
             * project in production, that's how I would do it anyway. And no, cleanup
             * after each test is not an option, because tests must be executed in
             * parallel (to reduce execution time and efficiently use all available
             * hardware resources) and be completely isolated from
             * one another.
             *
             * And yet, this tests still brings value, because it triggers the code,
             * and returns either with a success, or failure. Basically, we test what
             * we can in the given scenario.
             */

            Map leaderboard = QuizHelper.getLeaderboard(params)
            assert leaderboard.leaders.size() > 0

            assert leaderboard.leaders.get(0).userReferenceId
            assert leaderboard.leaders.get(0).totalScore

            assert leaderboard.headerText == "Leaderboard"
            assert leaderboard.scoreColumnTitle == "Total Score"
            assert leaderboard.usernameColumnTitle == "User"
            return leaderboard
        }
    }
}
