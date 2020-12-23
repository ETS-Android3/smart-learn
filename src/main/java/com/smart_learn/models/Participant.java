package com.smart_learn.models;

import lombok.Getter;
import lombok.ToString;
import org.springframework.web.socket.WebSocketSession;


@Getter
@ToString
public class Participant {

    private final WebSocketSession webSocketSession;
    private final boolean isTestAdmin;
    private final String userId;

    public Participant(WebSocketSession webSocketSession, boolean isTestAdmin, String userId) {
        this.webSocketSession = webSocketSession;
        this.isTestAdmin = isTestAdmin;
        this.userId = userId;
    }
}