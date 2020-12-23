package com.smart_learn.models;

import lombok.Getter;

@Getter
public class Response {
    private final int questionId;
    private final String question;
    private final String response;
    private final boolean correctResponse;

    public Response(int questionId, String question, String response, boolean correctResponse) {
        this.questionId = questionId;
        this.question = question;
        this.response = response;
        this.correctResponse = correctResponse;
    }
}
