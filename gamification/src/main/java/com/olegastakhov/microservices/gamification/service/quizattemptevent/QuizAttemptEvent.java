package com.olegastakhov.microservices.gamification.service.quizattemptevent;

public record QuizAttemptEvent(String attemptId, boolean correct, String userId) {
}
