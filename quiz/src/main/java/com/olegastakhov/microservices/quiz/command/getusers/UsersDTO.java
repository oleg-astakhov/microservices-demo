package com.olegastakhov.microservices.quiz.command.getusers;

import java.util.Map;

public record UsersDTO(Map<String, UserDTO> refToUser) {

}
