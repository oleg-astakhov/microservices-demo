package com.olegastakhov.microservices.quiz.service.quizes.common.api;

public record RandomQuizData<T>(T questionData, int index) {
}
