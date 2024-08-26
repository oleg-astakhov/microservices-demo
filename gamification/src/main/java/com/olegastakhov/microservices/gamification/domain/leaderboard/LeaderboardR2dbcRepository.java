package com.olegastakhov.microservices.gamification.domain.leaderboard;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

public interface LeaderboardR2dbcRepository extends R2dbcRepository<Leaderboard, Long> {
    @Modifying
    @Query("""
        UPDATE leaderboard 
        SET 
            total_score = :totalScore, 
            last_updated = :lastUpdated,
            version = version + 1 
        WHERE 1=1 
            AND pk = :pk
            AND version = :version
    """)
    Mono<Integer> updateIfVersionMatches(Long pk, Long totalScore, ZonedDateTime lastUpdated, Integer version);

    Mono<Leaderboard> findByUserReferenceId(String userReferenceId);

    @Query("SELECT * FROM leaderboard ORDER BY total_score DESC LIMIT 10")
    Flux<Leaderboard> findTop10();
}
