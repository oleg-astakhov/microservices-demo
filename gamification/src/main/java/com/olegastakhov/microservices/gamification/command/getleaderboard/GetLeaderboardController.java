package com.olegastakhov.microservices.gamification.command.getleaderboard;

import com.olegastakhov.microservices.gamification.common.ResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
        import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@RestController
public class GetLeaderboardController {

    @Autowired
    @Qualifier("GeneralPurposeVirtualThreadScheduler")
    private Scheduler virtualThreadScheduler;

    @Autowired
    GetLeaderboardServiceImpl getLeaderboardService;

    @GetMapping("/leaderboard/list")
    public Mono<ResultDTO<LeaderboardDTO>> getLeaderboard() {
        return getLeaderboardService.getLeaderboard()
                .subscribeOn(virtualThreadScheduler);
    }
}
