package com.smart_learn.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.socket.WebSocketSession;


@Getter
@ToString
public class Player {

    private final WebSocketSession webSocketSession;
    private final String publicIp;
    private final String localIp;

    @Setter
    private String symbol;

    @Setter
    private boolean canMove;

    public Player(WebSocketSession webSocketSession,String publicIp, String localIp, String symbol,boolean canMove) {
        this.webSocketSession = webSocketSession;
        this.publicIp = publicIp;
        this.localIp = localIp;
        this.symbol = symbol;
        this.canMove = canMove;
    }

}