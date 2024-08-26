package com.olegastakhov.microservices.gamification.command.getleaderboard;

public record LeaderDTO(String userReferenceId, Long totalScore) {

}
