package com.olegastakhov.microservices.quiz.service.quizes.common.api;

public interface QuestionGenerator<T> {
    String getId();
    String getCorrectAnswer(String questionItemId);

    QuizData generate();

    String getQuestion(String questionItemId);
}
