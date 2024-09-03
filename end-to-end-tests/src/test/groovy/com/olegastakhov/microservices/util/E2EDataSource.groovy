package com.olegastakhov.microservices.util

import groovy.sql.Sql

class E2EDataSource {
    private static E2EDataSource INSTANCE
    private Sql quizDatasource
    private Sql gamificationDatasource

    static synchronized E2EDataSource getInstance() {
        if (null == INSTANCE) {
            println("Creating ${E2EDataSource.class.getSimpleName()} instance...")
            INSTANCE = new E2EDataSource()
        }
        return INSTANCE
    }

    private E2EDataSource() {
        quizDatasource = Sql.newInstance('jdbc:postgresql://localhost:5433/quiz', 'microsappuser', 'microservices-database-pwd', 'org.postgresql.Driver')
        gamificationDatasource = Sql.newInstance('jdbc:postgresql://localhost:5433/gamification', 'microsappuser', 'microservices-database-pwd', 'org.postgresql.Driver')
    }

    Sql getQuizDatasource() {
        return quizDatasource
    }

    Sql getGamificationDatasource() {
        return gamificationDatasource
    }
}
