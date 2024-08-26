package com.olegastakhov.microservices.quiz.command.getpersonalstats;

import java.util.List;

public record PersonalStatsDTO(List<StatEntryDTO> stats, String headerText) {
}
