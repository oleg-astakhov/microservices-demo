package com.olegastakhov.microservices.gamification.service;

import com.olegastakhov.microservices.gamification.domain.SequenceReactiveRepository;
import com.olegastakhov.microservices.gamification.domain.score.Score;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

@Component
public class ScoreServiceImpl {
    @Autowired
    private SequenceReactiveRepository sequenceRepository;

    @Autowired
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    public Mono<Score> addNewScore(final String userReferenceId,
                                   final String attemptReferenceId,
                                   final Long score) {
        return sequenceRepository.getNextScorePk().flatMap(
                pk -> {
                    return r2dbcEntityTemplate.insert(new Score()
                            .setPk(pk)
                            .setDateCreated(ZonedDateTime.now())
                            .setValue(score)
                            .setAttemptReferenceId(attemptReferenceId)
                            .setUserReferenceId(userReferenceId));
                }
        );
    }
}
