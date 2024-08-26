package com.olegastakhov.microservices.quiz.command.submitanswer;

import com.olegastakhov.microservices.quiz.infrastructure.validation.FieldIsRequired;

public class SubmitAnswerData {
    private String questionId;
    private String questionItemId;
    @FieldIsRequired(i18nFieldKey = "field.answer")
    private String answer;
    @FieldIsRequired(i18nFieldKey = "field.username")
    private String username;

    public String getQuestionId() {
        return questionId;
    }

    public SubmitAnswerData setQuestionId(String questionId) {
        this.questionId = questionId;
        return this;
    }

    public String getQuestionItemId() {
        return questionItemId;
    }

    public SubmitAnswerData setQuestionItemId(String questionItemId) {
        this.questionItemId = questionItemId;
        return this;
    }

    public String getAnswer() {
        return answer;
    }

    public SubmitAnswerData setAnswer(String answer) {
        this.answer = answer;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public SubmitAnswerData setUsername(String username) {
        this.username = username;
        return this;
    }
}
