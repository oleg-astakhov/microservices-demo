package com.olegastakhov.microservices.quiz.command.getusers;

public class GetUsersModel {
    private Long pk;
    private String username;
    private String referenceId;

    public Long getPk() {
        return pk;
    }

    public GetUsersModel setPk(Long pk) {
        this.pk = pk;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public GetUsersModel setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public GetUsersModel setReferenceId(String referenceId) {
        this.referenceId = referenceId;
        return this;
    }
}
