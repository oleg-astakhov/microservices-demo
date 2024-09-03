package com.olegastakhov.microservices.quiz.service.quizes.common.api;

import java.util.ArrayList;
import java.util.List;

public class QuizData {
    private String questionId;
    private String questionItemId;
    private String question;
    private String correctAnswer;
    private List<String> options = new ArrayList<>();

    public String getQuestionId() {
        return questionId;
    }

    public QuizData setQuestionId(String questionId) {
        this.questionId = questionId;
        return this;
    }

    public String getQuestionItemId() {
        return questionItemId;
    }

    public QuizData setQuestionItemId(String questionItemId) {
        this.questionItemId = questionItemId;
        return this;
    }

    public String getQuestion() {
        return question;
    }

    public QuizData setQuestion(String question) {
        this.question = question;
        return this;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public QuizData setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
        return this;
    }

    public List<String> getOptions() {
        return options;
    }

    public QuizData setOptions(List<String> options) {
        this.options = options;
        return this;
    }
}
