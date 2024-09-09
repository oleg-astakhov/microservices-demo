package com.olegastakhov.microservices.quiz.command.getpersonalstats;

import com.olegastakhov.microservices.quiz.common.dto.ResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class GetPersonalStatsController {
    @Autowired
    GetPersonalStatsServiceImpl getPersonalStatsService;

    @GetMapping("/quiz/personal-stats")
    public Mono<ResultDTO<PersonalStatsDTO>> personalStats(final @RequestParam(value = "username", required = false) String username) {
        // TODO imagine user will come from security context, not from query param
        return getPersonalStatsService.getPersonalStats(username);
    }
}
