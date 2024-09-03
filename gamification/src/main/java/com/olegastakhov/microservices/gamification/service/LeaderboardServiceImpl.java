package com.olegastakhov.microservices.gamification.service;

import com.olegastakhov.microservices.gamification.common.ResultDTO;
import com.olegastakhov.microservices.gamification.domain.SequenceReactiveRepository;
import com.olegastakhov.microservices.gamification.domain.leaderboard.Leaderboard;
import com.olegastakhov.microservices.gamification.domain.leaderboard.LeaderboardR2dbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

@Component
public class LeaderboardServiceImpl {
    @Autowired
    private SequenceReactiveRepository sequenceRepository;

    @Autowired
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    @Autowired
    private LeaderboardR2dbcRepository leaderboardR2dbcRepository;

    public Mono<Void> updateTotals(final Long totalScore, String userReferenceId) {
        return Mono.defer(() -> {
            return leaderboardR2dbcRepository.findByUserReferenceId(userReferenceId)
                    .filterWhen(leader -> updateTotalScore(leader.getPk(), totalScore, leader.getVersion())
                            .then(Mono.just(true)))
                    .switchIfEmpty(Mono.defer(() -> createNewLeader(userReferenceId, totalScore)))
                    .then();
        });
    }

    private Mono<Leaderboard> createNewLeader(final String userReferenceId,
                                              final Long totalScore) {
        return sequenceRepository.getNextLeaderboardPk().flatMap(
                pk -> {
                    return r2dbcEntityTemplate.insert(new Leaderboard()
                            .setPk(pk)
                            .setDateCreated(ZonedDateTime.now())
                            .setTotalScore(totalScore)
                            .setUserReferenceId(userReferenceId));
                }
        );
    }

    private Mono<Integer> updateTotalScore(Long id, Long value, Integer currentVersion) {
        return leaderboardR2dbcRepository.updateIfVersionMatches(id, value, ZonedDateTime.now(), currentVersion)
                .flatMap(updatedCount -> {
                    if (updatedCount == 0) {
                        return Mono.error(new OptimisticLockingFailureException(String.format("Could not update Leaderboard row id [%s] due to version conflict", id)));
                    }
                    return Mono.just(updatedCount);
                });
    }
}
