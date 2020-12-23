package com.smart_learn.core;

import com.smart_learn.models.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

@Service
public class GameService {

    public static final int PLAYER_A_POSITION = 0;
    public static final int PLAYER_B_POSITION = 1;

    private final ApplicationContext context;

    private final Queue<String> codesQueue = new ConcurrentLinkedQueue<>();
    private final Map<String, GameConnectionData> connexionsMap = new ConcurrentHashMap<>();

    @Autowired
    public GameService(ApplicationContext context) {
        this.context = context;
    }

    @PostConstruct
    private void init(){
        generateCodes();
    }

    private void generateCodes(){
        AuditService.addLog(Level.INFO," Generating codes");
        for(int i = 5000; i < 5100; i++){
            codesQueue.add(String.valueOf(i));
        }
    }


    private void sendMessage(WebSocketSession webSocketSession,String message){
        try {
            if(webSocketSession.isOpen()) {
                webSocketSession.sendMessage(new TextMessage(message));
                return;
            }

            AuditService.addLog(Level.WARNING,Logs.FUNCTION + "[sendMessage] Unable to send message [" + message +
                    "] . No web socket available ");

        } catch (IOException e) {
            e.printStackTrace();
            AuditService.addLog(Level.WARNING,Logs.UNEXPECTED_ERROR + Logs.FUNCTION + "[sendMessage] message [" +
                    message + "] not sent[ " + e + "]");
        }
    }

    private void sendErrorResponse(WebSocketSession webSocketSession,String transmissionCode, String message){
        HashMap<String,String> tmp = new HashMap<>();
        tmp.put(StrictCodes.TRANSMISSION_CODE,transmissionCode);
        tmp.put(StrictCodes.INFO_MESSAGE,message);
        sendMessage(webSocketSession,createJsonObjectResponse(tmp).toString());
    }



    public void onTextMessageReceived(WebSocketSession webSocketSession,String payload) {

        JSONObject jsonObject;
        // extract payload
        try {
            jsonObject = new JSONObject(payload);
        }
        catch (JSONException e) {
            e.printStackTrace();
            AuditService.addLog(Level.SEVERE,Logs.UNEXPECTED_ERROR + Logs.FUNCTION +
                    "[handleTextMessage] Failed to Parse Json [" + payload + "] . Error description  [" + e + "]");
            sendErrorResponse(webSocketSession,StrictCodes.GAME_ERROR,"payload [" + payload + "] is not good");
            return;
        }

        // check payload
        try{
            switch (jsonObject.getString(StrictCodes.TRANSMISSION_CODE)){
                case StrictCodes.NEW_CONNEXION -> {
                    Player player = new Player(webSocketSession,
                            jsonObject.getString(StrictCodes.PUBLIC_IP),
                            jsonObject.getString(StrictCodes.LOCAL_IP),
                            jsonObject.getString(StrictCodes.PLAYER_SYMBOL),
                            Boolean.parseBoolean(jsonObject.getString(StrictCodes.FIRST_TO_MOVE))
                    );
                    createGameSession(player,Integer.parseInt(jsonObject.getString(StrictCodes.PLAYERS_NUMBER)),
                            jsonObject.getString(StrictCodes.OPPONENT_SYMBOL));
                }

                case StrictCodes.JOIN_CONNEXION -> {
                    Player player = new Player(webSocketSession,
                            jsonObject.getString(StrictCodes.PUBLIC_IP),
                            jsonObject.getString(StrictCodes.LOCAL_IP),
                            jsonObject.getString(StrictCodes.PLAYER_SYMBOL),
                            Boolean.parseBoolean(jsonObject.getString(StrictCodes.FIRST_TO_MOVE))
                    );
                    joinGameSession(jsonObject.getString(StrictCodes.GAME_CODE),player);
                }

                // broadcast move to all players/supporters from connection
                case StrictCodes.GAME_MOVE -> broadcastGameMove(webSocketSession,
                        jsonObject.getString(StrictCodes.GAME_CODE),jsonObject.getString(StrictCodes.PLAYER_SYMBOL),
                        jsonObject.getString(StrictCodes.PLAYER_MOVE));

                // broadcast move to all players/supporters from connection
                case StrictCodes.GAME_CHAT_MESSAGE -> broadcastGameChatMessage(webSocketSession,
                        jsonObject.getString(StrictCodes.GAME_CODE),
                        jsonObject.getString(StrictCodes.USER_ID),
                        jsonObject.getString(StrictCodes.CHAT_MESSAGE_BODY),
                        jsonObject.getString(StrictCodes.CHAT_MESSAGE_TIME));

                case StrictCodes.STOP_GAME -> {
                    // broadcast stop message to all players/supporters from connection
                    stopGameSession(webSocketSession,jsonObject.getString(StrictCodes.GAME_CODE),
                            jsonObject.getString(StrictCodes.PLAYER_SYMBOL));
                }


                default -> {
                    AuditService.addLog(Level.SEVERE,Logs.UNEXPECTED_ERROR + Logs.FUNCTION + "[handleTextMessage] " +
                            StrictCodes.TRANSMISSION_CODE + "[" + jsonObject.getString(StrictCodes.TRANSMISSION_CODE) +
                            "] is not valid");
                    sendErrorResponse(webSocketSession,StrictCodes.GAME_ERROR,StrictCodes.TRANSMISSION_CODE + " [" +
                            jsonObject.getString(StrictCodes.TRANSMISSION_CODE) + "] not good");
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            AuditService.addLog(Level.SEVERE,Logs.UNEXPECTED_ERROR + Logs.FUNCTION + "[handleTextMessage] Failed " +
                    "to Parse Json [" + jsonObject + "] " + e);
            sendErrorResponse(webSocketSession,StrictCodes.GAME_ERROR," payload [" + jsonObject +
                    "] not good");
        }
    }



    private GameConnectionData checkConnection(WebSocketSession webSocketSession, String code, boolean sendMessage){
        GameConnectionData connection = connexionsMap.get(code);

        // if connexion does not exist return error
        if(connection == null && sendMessage){
            sendErrorResponse(webSocketSession,StrictCodes.GAME_ERROR,
                    " connection for game code [" + code + "] not found");
            return null;
        }

        return connection;
    }

    private void closeWebSocketConnections(GameConnectionData connection){
        connection.getPlayers().forEach(p -> {
            // close web sockets
            try {
                if(p.getWebSocketSession().isOpen()){
                    p.getWebSocketSession().close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    public JSONObject createJsonObjectResponse(HashMap<String,String> payloadValues){
        JSONObject payload = new JSONObject();

        // create payload
        payloadValues.forEach((key, value) -> {
            try{
                payload.put(key,value);
            }
            catch (JSONException e) {
                e.printStackTrace();
                AuditService.addLog(Level.SEVERE,Logs.UNEXPECTED_ERROR + Logs.FUNCTION + "[createJsonObjectResponse]" +
                        " can not create JsonObject [" + payloadValues + "] " + e);
                // TODO: here game can be canceled
            }
        });

        return payload;
    }

    public void createGameSession(Player player, int playersNumber, String opponentSymbol){

        // double check player symbol
        if(!(player.getSymbol().equals(StrictCodes.PLAYER_A_SYMBOL) || player.getSymbol().equals(StrictCodes.PLAYER_B_SYMBOL))){
            sendErrorResponse(player.getWebSocketSession(),StrictCodes.GAME_ERROR,
                    " Player symbol [" + player.getSymbol() + "] is not valid");
            return;
        }

        // double check opponent symbol
        if(!(opponentSymbol.equals(StrictCodes.PLAYER_A_SYMBOL) || opponentSymbol.equals(StrictCodes.PLAYER_B_SYMBOL))){
            sendErrorResponse(player.getWebSocketSession(),StrictCodes.GAME_ERROR,
                    " Opponent symbol [" + player.getSymbol() + "] is not valid");
            return;
        }

        // double check if both have same symbol
        if((player.getSymbol().equals(opponentSymbol))){
            sendErrorResponse(player.getWebSocketSession(),StrictCodes.GAME_ERROR,
                    " Player symbol [" + player.getSymbol() + "] is same as opponent symbol");
            return;
        }

        // no game code is available
        if(codesQueue.isEmpty()){
            sendErrorResponse(player.getWebSocketSession(),StrictCodes.GAME_ERROR,
                    " No code available. Server is full. Game was not created");
            return;
        }

        // get game code
        String gameCode = codesQueue.remove();

        // create new connexion
        GameConnectionData gameConnectionData = context.getBean(GameConnectionData.class);

        // add connection data
        gameConnectionData.getPlayers().add(player);
        gameConnectionData.setGameCode(gameCode);
        gameConnectionData.setMaxPlayers(playersNumber);

        // player A position is PLAYER_A_POSITION
        gameConnectionData.getSymbolList().add(player.getSymbol());
        // player B position is PLAYER_B_POSITION
        gameConnectionData.getSymbolList().add(opponentSymbol);

        // add connexion to connexions map
        connexionsMap.put(gameCode, gameConnectionData);

        HashMap<String,String> tmp = new HashMap<>();
        tmp.put(StrictCodes.TRANSMISSION_CODE,StrictCodes.GAME_CODE_RECEIVED);
        tmp.put(StrictCodes.GAME_CODE,gameCode);
        sendMessage(player.getWebSocketSession(),createJsonObjectResponse(tmp).toString());
    }

    public void joinGameSession(String gameCode, Player player){

        GameConnectionData connection = checkConnection(player.getWebSocketSession(), gameCode,false);

        if(connection == null){
            sendErrorResponse(player.getWebSocketSession(), StrictCodes.WRONG_CODE,
                    " connection for game code [" + gameCode + "] not found");
            try {
                // disconnect player
                if(player.getWebSocketSession().isOpen()){
                    player.getWebSocketSession().close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // game code was good
        // check if player already joined
        for(Player p: connection.getPlayers()){

            if(p.getWebSocketSession().isOpen() && player.getWebSocketSession().isOpen() &&
                    p.getWebSocketSession().getId().equals(player.getWebSocketSession().getId())){

                sendErrorResponse(p.getWebSocketSession(), StrictCodes.GAME_ERROR,
                        " You already joined joined for game " + gameCode);

                return;
            }
        }

        // check if all players already joined
        // TODO: if is a public game then player can be joined as a visitor
        if(connection.getMaxPlayers() == connection.getPlayers().size()){
            sendErrorResponse(player.getWebSocketSession(), StrictCodes.GAME_ERROR,
                    " All players joined for game " + gameCode);
            return;
        }


        // check if is localhost play (compare public ip`s)
        String serverIp = connection.getPlayers().get(PLAYER_A_POSITION).getPublicIp();

        /*
        // is localhost play if only players have same public ip
        if(connection.getPlayers().stream().filter(p -> p.getPublicIp().equals(serverIp)).count() ==
                connection.getPlayers().size()){
            connection.setLocalhostPlay(true);
            connection.setLocalIp(connection.getPlayers().get(PLAYER_A_POSITION).getLocalIp());
        }
         */




        // set player symbol
        player.setSymbol(connection.getSymbolList().get(PLAYER_B_POSITION));

        // set if player can move
        player.setCanMove(!connection.getPlayers().get(PLAYER_A_POSITION).isCanMove());

        // add player to connexion
        connection.getPlayers().add(player);

        // if all players joined notify players that game can start
        if(connection.getMaxPlayers() == connection.getPlayers().size()){

            // create start game common response
            HashMap<String,String> tmp = new HashMap<>();
            tmp.put(StrictCodes.TRANSMISSION_CODE,StrictCodes.START_GAME);
            tmp.put(StrictCodes.LOCALHOST,String.valueOf(connection.isLocalhostPlay()));
            tmp.put(StrictCodes.LOCAL_IP,connection.getLocalIp());

            for(Player p: connection.getPlayers()){
                /*
                if(!player.getWebSocketSession().isOpen()){
                    // TODO: stop game here because one player is disconnected
                    return;
                }
                 */
                tmp.put(StrictCodes.WEBSOCKET_SESSION_ID, p.getWebSocketSession().getId());
                tmp.put(StrictCodes.PLAYER_SYMBOL,p.getSymbol());
                tmp.put(StrictCodes.FIRST_TO_MOVE,String.valueOf(p.isCanMove()));
                sendMessage(p.getWebSocketSession(),createJsonObjectResponse(tmp).toString());
            }
        }
    }

    public void broadcastGameMove(WebSocketSession webSocketSession, String gameCode, String symbol, String move){

        GameConnectionData connection = checkConnection(webSocketSession,gameCode,true);
        if(connection == null){
            // close session
            try {
                if(webSocketSession.isOpen()){
                    webSocketSession.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        HashMap<String,String> tmp = new HashMap<>();
        tmp.put(StrictCodes.TRANSMISSION_CODE,StrictCodes.GAME_MOVE);
        tmp.put(StrictCodes.WEBSOCKET_SESSION_ID, webSocketSession.getId());

        for(Player p: connection.getPlayers()){
           /*
            if(!player.getWebSocketSession().isOpen()){
                // TODO: stop game here because one player is disconnected
                return;
            }
             */
            tmp.put(StrictCodes.PLAYER_MOVE,move);
            tmp.put(StrictCodes.PLAYER_SYMBOL,symbol);
            sendMessage(p.getWebSocketSession(),createJsonObjectResponse(tmp).toString());
        }
    }

    public void broadcastGameChatMessage(WebSocketSession webSocketSession, String gameCode, String userId,
                                         String message, String time){

        GameConnectionData connection = checkConnection(webSocketSession,gameCode,true);
        if(connection == null){
            // close session
            try {
                if(webSocketSession.isOpen()){
                    webSocketSession.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        HashMap<String,String> tmp = new HashMap<>();
        tmp.put(StrictCodes.TRANSMISSION_CODE,StrictCodes.GAME_CHAT_MESSAGE);
        tmp.put(StrictCodes.WEBSOCKET_SESSION_ID, webSocketSession.getId());

        for(Player p: connection.getPlayers()){
           /*
            if(!player.getWebSocketSession().isOpen()){
                // TODO: stop game here because one player is disconnected
                return;
            }
             */
            tmp.put(StrictCodes.USER_ID,userId);
            tmp.put(StrictCodes.CHAT_MESSAGE_BODY,message);
            tmp.put(StrictCodes.CHAT_MESSAGE_TIME,time);
            sendMessage(p.getWebSocketSession(),createJsonObjectResponse(tmp).toString());
        }
    }

    public void stopGameSession(WebSocketSession webSocketSession, String gameCode,String symbol){

        GameConnectionData connection = checkConnection(webSocketSession,gameCode,false);

        HashMap<String,String> tmp = new HashMap<>();
        tmp.put(StrictCodes.TRANSMISSION_CODE,StrictCodes.STOP_GAME);

        // if connexion does not exist return error
        if(connection == null){
            tmp.put(StrictCodes.INFO_MESSAGE," connection for game code [" + gameCode + "] not found. Game stopped already.");
            sendMessage(webSocketSession,createJsonObjectResponse(tmp).toString());
        }
        else{
            // broadcast stopping message anc close sessions

            tmp.put(StrictCodes.INFO_MESSAGE," Game [" + gameCode + "] stopped successfully.");
            tmp.put(StrictCodes.PLAYER_SYMBOL,symbol);

            for (Player p: connection.getPlayers()){
                if(p != null) {
                    sendMessage(p.getWebSocketSession(),createJsonObjectResponse(tmp).toString());
                }
            }

            closeWebSocketConnections(connection);

            // remove connection
            connexionsMap.remove(gameCode);
            codesQueue.add(gameCode);
        }
    }

}
