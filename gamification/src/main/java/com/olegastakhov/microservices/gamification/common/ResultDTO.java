package com.olegastakhov.microservices.gamification.common;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultDTO<T> {
    T data; // nullable
    private String message; // nullable

    public ResultDTO(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public ResultDTO<T> setData(T data) {
        this.data = data;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResultDTO<T> setMessage(String message) {
        this.message = message;
        return this;
    }
}

