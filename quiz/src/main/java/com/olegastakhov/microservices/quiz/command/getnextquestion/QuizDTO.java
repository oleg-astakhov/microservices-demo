package com.olegastakhov.microservices.quiz.command.getnextquestion;

import java.util.List;

public class QuizDTO {
    private String questionId;
    private String questionItemId;
    private String question;
    private List<QuizOptionDTO> options;
    private String submitAnswerButtonCaption;
    private String usernameInputPlaceholderCaption;

    /**
     * Will only be initialized when environment is e2e-test (end-to-end tests)
     * This is because question are random, and a test cannot know the correct
     * answer to be able to test success flow.
     * Note that if the value is null (for non-e2e-test environment) then
     * the property will not even be rendered to JSON
     */
    private String correctAnswer;

    public String getQuestionId() {
        return questionId;
    }

    public QuizDTO setQuestionId(String questionId) {
        this.questionId = questionId;
        return this;
    }

    public String getQuestionItemId() {
        return questionItemId;
    }

    public QuizDTO setQuestionItemId(String questionItemId) {
        this.questionItemId = questionItemId;
        return this;
    }

    public String getQuestion() {
        return question;
    }

    public QuizDTO setQuestion(String question) {
        this.question = question;
        return this;
    }

    public List<QuizOptionDTO> getOptions() {
        return options;
    }

    public QuizDTO setOptions(List<QuizOptionDTO> options) {
        this.options = options;
        return this;
    }

    public String getSubmitAnswerButtonCaption() {
        return submitAnswerButtonCaption;
    }

    public QuizDTO setSubmitAnswerButtonCaption(String submitAnswerButtonCaption) {
        this.submitAnswerButtonCaption = submitAnswerButtonCaption;
        return this;
    }

    public String getUsernameInputPlaceholderCaption() {
        return usernameInputPlaceholderCaption;
    }

    public QuizDTO setUsernameInputPlaceholderCaption(String usernameInputPlaceholderCaption) {
        this.usernameInputPlaceholderCaption = usernameInputPlaceholderCaption;
        return this;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public QuizDTO setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
        return this;
    }
}
