package com.smart_learn.web;

import com.smart_learn.core.AuditService;
import com.smart_learn.core.GameService;
import com.smart_learn.core.ThreadPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.nio.ByteBuffer;
import java.util.logging.Level;

//https://learn.vonage.com/blog/2018/10/08/create-websocket-server-spring-boot-dr/
@Component
@Scope("prototype")
public class WebSocketHandler extends AbstractWebSocketHandler {

    private final GameService gameService;
    private final ThreadPoolService threadPoolService;

    @Autowired
    public WebSocketHandler(GameService gameService, ThreadPoolService threadPoolService) {
        this.gameService = gameService;
        this.threadPoolService = threadPoolService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        AuditService.addLog(Level.INFO,"Connexion established [" + session.getId() + "]");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        AuditService.addLog(Level.INFO,"Connexion stopped [" + session.getId() + "]");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        String payload = message.getPayload();
        AuditService.addLog(Level.INFO,"received payload: " + payload);
        threadPoolService.execute(() -> gameService.onTextMessageReceived(session,payload));
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        super.handleBinaryMessage(session, message);
        AuditService.addLog(Level.INFO,"New Binary Message Received " + message);
        message = new BinaryMessage(ByteBuffer.allocateDirect(262217));
        session.sendMessage(message);
    }
}
