package com.olegastakhov.microservices.util


import groovy.sql.GroovyRowResult

class E2EQuery {
    final static E2ESqlClient quizClient = new E2ESqlClient(E2EDataSource.getInstance().getQuizDatasource())
    final static E2ESqlClient gamificationClient = new E2ESqlClient(E2EDataSource.getInstance().getGamificationDatasource())

    static class AppUser {
        static GroovyRowResult getRow(final String username) {
            return quizClient.getRow("SELECT au.* FROM app_user au WHERE au.username = :pUsername", [pUsername: username])
        }

        static Optional<GroovyRowResult> findRow(final String username) {
            return quizClient.findRow("SELECT au.* FROM app_user au WHERE au.username = :pUsername", [pUsername: username])
        }

        static List<GroovyRowResult> list(final List<String> usernames) {
            def usernamesAsDelimitedString = usernames.stream().map { "'${it}'" }.toList().join(",")
            def sql = "SELECT au.* FROM app_user au WHERE au.username IN (${usernamesAsDelimitedString})"
            return quizClient.getSql().rows(sql.toString())
        }
    }

    static class QueryAttempt {
        static List<GroovyRowResult> listDescending(final String username) {
            return quizClient.getSql().rows("""
                SELECT qa.* 
                FROM app_user au 
                JOIN quiz_attempt qa ON (qa.app_user_fk = au.pk)
                WHERE 1=1
                    AND au.username = :pUsername
                ORDER BY qa.pk DESC                
            """, [pUsername: username])
        }
    }

    static class Score {
        static long count(final String userReferenceId) {
            return gamificationClient.getRow("""
                SELECT COUNT(*) FROM score s WHERE s.user_reference_id = :pUserReferenceId
            """, [pUserReferenceId: userReferenceId]).count
        }

        static List<GroovyRowResult> listDescending(final String userReferenceId) {
            return gamificationClient.getSql().rows("""
                SELECT s.* FROM score s WHERE s.user_reference_id = :pUserReferenceId ORDER BY s.pk DESC                
            """, [pUserReferenceId: userReferenceId])
        }

        static GroovyRowResult getLastRow(final String userReferenceId) {
            return gamificationClient.getRow("""
                SELECT s.* 
                FROM score s 
                WHERE 1=1
                    AND s.user_reference_id = :pUserReferenceId
                ORDER BY s.pk DESC
                LIMIT 1
            """, [pUserReferenceId: userReferenceId])
        }
    }

    static class Leaderboard {
        static GroovyRowResult get(final String userReferenceId) {
            return gamificationClient.getRow("""
                SELECT l.* FROM leaderboard l WHERE l.user_reference_id = :pUserReferenceId
            """, [pUserReferenceId: userReferenceId])
        }

        static long count(final String userReferenceId) {
            return gamificationClient.getRow("""
                SELECT COUNT(*) FROM leaderboard l WHERE l.user_reference_id = :pUserReferenceId
            """, [pUserReferenceId: userReferenceId]).count
        }
    }
}
