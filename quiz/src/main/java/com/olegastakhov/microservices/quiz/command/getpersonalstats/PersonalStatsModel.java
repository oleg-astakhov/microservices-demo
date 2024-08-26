package com.olegastakhov.microservices.quiz.command.getpersonalstats;

public class PersonalStatsModel {
    private Long pk;
    private String question;
    private boolean correct;

    public Long getPk() {
        return pk;
    }

    public PersonalStatsModel setPk(Long pk) {
        this.pk = pk;
        return this;
    }

    public String getQuestion() {
        return question;
    }

    public PersonalStatsModel setQuestion(String question) {
        this.question = question;
        return this;
    }

    public boolean isCorrect() {
        return correct;
    }

    public PersonalStatsModel setCorrect(boolean correct) {
        this.correct = correct;
        return this;
    }
}
