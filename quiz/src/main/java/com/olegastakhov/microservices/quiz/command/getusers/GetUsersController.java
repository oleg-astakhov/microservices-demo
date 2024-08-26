package com.olegastakhov.microservices.quiz.command.getusers;

import com.olegastakhov.microservices.quiz.common.dto.ResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class GetUsersController {
    @Autowired
    GetUsersServiceImpl getUsersService;

    @GetMapping("/quiz/users")
    public Mono<ResultDTO<UsersDTO>> listUsers(final @RequestParam List<String> refs) {
        return getUsersService.getUsers(refs);
    }
}
