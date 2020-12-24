package com.smart_learn.activities.ui.test;

import lombok.Getter;

@Getter
public class QuestionModel {

    private final String question;
    private final String questionResponse;
    private String participantResponse = "";
    private boolean isCorrectResponse = false;

    public QuestionModel(String question, String questionResponse) {
        this.question = question;
        this.questionResponse = questionResponse;
    }

    public void setParticipantResponse(String participantResponse) {
        this.participantResponse = participantResponse;
        this.isCorrectResponse = participantResponse.toLowerCase().equals(questionResponse.toLowerCase());
    }
}
