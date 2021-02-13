package com.smart_learn.presenter.activities.ui.test;

import lombok.Getter;

@Getter
public class ChatMessageModel {

    private final String userId;
    private String text;
    private final String time;
    private final int viewType;

    public ChatMessageModel(String userId, String text, String time, int viewType) {
        this.userId = userId;
        this.text = text;
        this.time = time;
        this.viewType = viewType;
    }
}