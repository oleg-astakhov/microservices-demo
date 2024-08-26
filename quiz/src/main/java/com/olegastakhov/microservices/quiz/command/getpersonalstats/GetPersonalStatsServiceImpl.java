package com.olegastakhov.microservices.quiz.command.getpersonalstats;

import com.olegastakhov.microservices.quiz.command.getnextquestion.QuizDTO;
import com.olegastakhov.microservices.quiz.common.dto.ResultDTO;
import com.olegastakhov.microservices.quiz.domain.user.AppUser;
import com.olegastakhov.microservices.quiz.domain.user.UserRepository;
import com.olegastakhov.microservices.quiz.infrastructure.localization.LocalizationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;
import java.util.Optional;

@Service
public class GetPersonalStatsServiceImpl {
    @Autowired
    PersonalStatsRepository personalStatsRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    LocalizationServiceImpl localization;

    @Autowired
    @Qualifier("GeneralPurposeVirtualThreadScheduler")
    private Scheduler virtualThreadScheduler;

    public Mono<ResultDTO<PersonalStatsDTO>> getPersonalStats(final String username) {
        // TODO user will come from security context
        Assert.hasLength(username, "username is blank when not expected");
        return Mono.fromCallable(() -> new ResultDTO<>(doGetPersonalStats(username)))
                .subscribeOn(virtualThreadScheduler);
    }

    private PersonalStatsDTO doGetPersonalStats(final String username) {
        final Optional<AppUser> user = userRepository.findByUsername(username); // TODO from security context
        if (user.isEmpty()) {
            throw new IllegalStateException(String.format("User with username [%s] does not exist", username));
        }
        return doGetPersonalStats(user.get().getPk());
    }

    private PersonalStatsDTO doGetPersonalStats(final Long userPk) {
        final List<PersonalStatsModel> personalStats = personalStatsRepository.listPersonalStats(userPk);
        return new PersonalStatsDTO(map(personalStats), localization.getLocalizedMessage("quiz.personalStatsHeaderText"));
    }

    private List<StatEntryDTO> map(final List<PersonalStatsModel> personalStats) {
        return personalStats.stream()
                .map(it -> new StatEntryDTO(it.getQuestion(), it.isCorrect()))
                .toList();
    }
}
