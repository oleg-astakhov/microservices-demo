package com.olegastakhov.microservices.gamification.command.getleaderboard;

import com.olegastakhov.microservices.gamification.common.ResultDTO;
import com.olegastakhov.microservices.gamification.domain.leaderboard.Leaderboard;
import com.olegastakhov.microservices.gamification.domain.leaderboard.LeaderboardR2dbcRepository;
import com.olegastakhov.microservices.gamification.infrastructure.localization.LocalizationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GetLeaderboardServiceImpl {

    @Autowired
    private LeaderboardR2dbcRepository leaderboardR2dbcRepository;
    @Autowired
    LocalizationServiceImpl localization;

    public Mono<ResultDTO<LeaderboardDTO>> getLeaderboard() {
        return leaderboardR2dbcRepository.findTop10()
                .flatMap(it -> Flux.just(map(it)))
                .collectList()
                .flatMap(list -> Mono.just(new ResultDTO<>(new LeaderboardDTO(
                        list,
                        localization.getLocalizedMessage("quiz.leaderboardHeaderText"),
                        localization.getLocalizedMessage("quiz.leaderboardScoreColumnTitle"),
                        localization.getLocalizedMessage("quiz.leaderboardUsernameColumnTitle")
                ))));
    }

    private LeaderDTO map(Leaderboard leaderboard) {
        return new LeaderDTO(leaderboard.getUserReferenceId(), leaderboard.getTotalScore());
    }
}
