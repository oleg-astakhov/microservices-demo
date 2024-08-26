package com.olegastakhov.microservices.quiz.command.submitanswer;

public class SubmitAnswerResultDTO {
    private boolean correct;
    private String motivationMessage;
    private String nextQuestionButtonCaption;

    public boolean isCorrect() {
        return correct;
    }

    public SubmitAnswerResultDTO setCorrect(boolean correct) {
        this.correct = correct;
        return this;
    }

    public String getMotivationMessage() {
        return motivationMessage;
    }

    public SubmitAnswerResultDTO setMotivationMessage(String motivationMessage) {
        this.motivationMessage = motivationMessage;
        return this;
    }

    public String getNextQuestionButtonCaption() {
        return nextQuestionButtonCaption;
    }

    public SubmitAnswerResultDTO setNextQuestionButtonCaption(String nextQuestionButtonCaption) {
        this.nextQuestionButtonCaption = nextQuestionButtonCaption;
        return this;
    }
}
