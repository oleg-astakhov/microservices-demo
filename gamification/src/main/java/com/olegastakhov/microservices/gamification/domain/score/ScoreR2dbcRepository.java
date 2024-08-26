package com.olegastakhov.microservices.gamification.domain.score;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface ScoreR2dbcRepository extends R2dbcRepository<Score, Long> {
    @Query("SELECT SUM(s.value) FROM Score s WHERE s.user_reference_id = :userId GROUP BY s.user_reference_id")
    Mono<Long> getTotalScoreForUser(String userReferenceId);

    Mono<Score> findByAttemptReferenceId(String attemptReferenceId);
}
