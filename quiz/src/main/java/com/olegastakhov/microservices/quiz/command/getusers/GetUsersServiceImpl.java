package com.olegastakhov.microservices.quiz.command.getusers;

import com.olegastakhov.microservices.quiz.common.dto.ResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GetUsersServiceImpl {
    @Autowired
    @Qualifier("GeneralPurposeVirtualThreadScheduler")
    private Scheduler virtualThreadScheduler;

    @Autowired
    GetUsersRepository getUsersRepository;

    public Mono<ResultDTO<UsersDTO>> getUsers(final List<String> userRefs) {
        if (userRefs.isEmpty()) {
            return Mono.just(new ResultDTO<>(new UsersDTO(new HashMap<>())));
        }
        return Mono.fromCallable(() -> new ResultDTO<>(doGetUsers(userRefs)))
                .subscribeOn(virtualThreadScheduler);
    }

    private UsersDTO doGetUsers(final List<String> userRefs) {
        final List<GetUsersModel> users = getUsersRepository.listUsers(userRefs);
        if (users.size() != userRefs.size()) {
            throw new IllegalArgumentException(String.format("Could not find some users by provided ref ids. Ref ids count [%s], matching users count [%s]. Incoming ref ids: %s", userRefs.size(), users.size(), userRefs));
        }

        final Map<String, UserDTO> refToUser = users.stream()
                .map(this::map)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new UsersDTO(refToUser);
    }

    private Map.Entry<String, UserDTO> map(GetUsersModel user) {
        return Map.entry(user.getReferenceId(), new UserDTO(user.getUsername()));
    }
}
