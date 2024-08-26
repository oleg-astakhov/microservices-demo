package com.olegastakhov.microservices.quiz.command.submitanswer;

public record QuizAttemptEvent(String attemptId, boolean correct, String userId) {
}
