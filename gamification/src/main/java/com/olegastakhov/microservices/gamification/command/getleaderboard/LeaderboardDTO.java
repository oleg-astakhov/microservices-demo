package com.olegastakhov.microservices.gamification.command.getleaderboard;

import java.util.List;

public record LeaderboardDTO (List<LeaderDTO> leaders,
                              String headerText,
                              String scoreColumnTitle,
                              String usernameColumnTitle) {
}
