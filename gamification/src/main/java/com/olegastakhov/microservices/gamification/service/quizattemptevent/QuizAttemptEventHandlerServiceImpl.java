package com.olegastakhov.microservices.gamification.service.quizattemptevent;

import com.olegastakhov.microservices.gamification.service.LeaderboardServiceImpl;
import com.olegastakhov.microservices.gamification.domain.score.ScoreR2dbcRepository;
import com.olegastakhov.microservices.gamification.service.ScoreServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Service
public class QuizAttemptEventHandlerServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(QuizAttemptEventHandlerServiceImpl.class);

    @Autowired
    @Qualifier("GeneralPurposeVirtualThreadScheduler")
    private Scheduler virtualThreadScheduler;
    @Autowired
    private TransactionalOperator transactionalOperator;
    @Autowired
    ScoreServiceImpl scoreServiceImpl;
    @Autowired
    private ScoreR2dbcRepository scoreR2dbcRepository;

    @Autowired
    private LeaderboardServiceImpl leaderboardService;

    public void processEvent(QuizAttemptEvent event) {
        Assert.hasLength(event.attemptId(), "attemptId is blank when not expected");
        Assert.hasLength(event.userId(), "userId is null blank not expected");
        Mono.defer(() -> process(event))
                .subscribeOn(virtualThreadScheduler)
                .as(transactionalOperator::transactional)
                .subscribe(null, ex -> log.error(ex.getMessage(), ex));
    }

    private Mono<Void> process(final QuizAttemptEvent attempt) {
        return scoreR2dbcRepository.findByAttemptReferenceId(attempt.userId())
                .hasElement()
                .flatMap(attemptExists -> {
                    if (attemptExists) {
                        log.debug(String.format("Received duplicate attempt id [%s]. This value will be ignored...", attempt.attemptId()));
                        return Mono.empty();
                    } else {
                        return Mono.defer(() -> scoreServiceImpl.addNewScore(attempt.userId(), attempt.attemptId(), 10L)
                                .then(scoreR2dbcRepository.getTotalScoreForUser(attempt.userId()))
                                .flatMap(total -> leaderboardService.updateTotals(total, attempt.userId()))
                        );
                    }
                });

    }
}
