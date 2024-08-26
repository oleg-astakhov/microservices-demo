package com.olegastakhov.microservices.gamification.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class SequenceReactiveRepository {
    @Qualifier("r2dbcDatabaseClient")
    @Autowired
    private DatabaseClient databaseClient;

    public Mono<Long> getNextScorePk() {
        return databaseClient.sql("SELECT NEXTVAL('score__seq')")
                .map(row -> row.get(0, Long.class))
                .one();
    }

}
