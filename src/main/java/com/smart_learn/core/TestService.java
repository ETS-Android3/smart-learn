package com.smart_learn.core;

import com.smart_learn.models.Participant;
import com.smart_learn.models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
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
public class TestService {

    private final ApplicationContext context;

    private final Queue<String> codesQueue = new ConcurrentLinkedQueue<>();
    private final Map<String, TestConnectionData> connexionsMap = new ConcurrentHashMap<>();

    @Autowired
    public TestService(ApplicationContext context) {
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
        }
    }

    private void sendErrorResponse(WebSocketSession webSocketSession,String transmissionCode, String message){
        HashMap<String,String> tmp = new HashMap<>();
        tmp.put(StrictCodes.TRANSMISSION_CODE,transmissionCode);
        tmp.put(StrictCodes.INFO_MESSAGE,message);
        sendMessage(webSocketSession,createJsonObjectResponse(tmp,null,null).toString());
    }



    public void onTextMessageReceived(WebSocketSession webSocketSession,String payload) {

        JSONObject jsonObject;
        // extract payload
        try {
            jsonObject = new JSONObject(payload);
        }
        catch (JSONException e) {
            sendErrorResponse(webSocketSession,StrictCodes.TEST_ERROR,"payload [" + payload + "] is not good");
            e.printStackTrace();
            return;
        }

        // check payload
        try{
            switch (jsonObject.getString(StrictCodes.TRANSMISSION_CODE)){
                case StrictCodes.NEW_CONNEXION -> {
                    Participant participant = new Participant(webSocketSession,true,
                            jsonObject.getString(StrictCodes.USER_ID));

                    createTestSession(participant,
                            Integer.parseInt(jsonObject.getString(StrictCodes.PARTICIPANTS_NUMBER)),
                            jsonObject.getJSONArray(StrictCodes.TEST_QUESTIONS));
                }

                case StrictCodes.JOIN_CONNEXION -> {
                    Participant participant = new Participant(webSocketSession,false,
                            jsonObject.getString(StrictCodes.USER_ID));

                    joinTestSession(jsonObject.getString(StrictCodes.TEST_CODE), participant);
                }

                case StrictCodes.START_TEST -> {
                    broadcastStartTest(webSocketSession,jsonObject.getString(StrictCodes.TEST_CODE));
                }

                case StrictCodes.TEST_RESPONSE -> {
                    Response response = new Response(Integer.parseInt(jsonObject.getString(StrictCodes.QUESTION_ID)),
                            jsonObject.getString(StrictCodes.QUESTION),jsonObject.getString(StrictCodes.RESPONSE),
                            Boolean.parseBoolean(jsonObject.getString(StrictCodes.CORRECT_RESPONSE)));

                    registerResponse(webSocketSession,response,jsonObject.getString(StrictCodes.USER_ID),
                            jsonObject.getString(StrictCodes.TEST_CODE));
                }

                case StrictCodes.TEST_CHAT_MESSAGE -> broadcastTestChatMessage(webSocketSession,
                        jsonObject.getString(StrictCodes.TEST_CODE),
                        jsonObject.getString(StrictCodes.USER_ID),
                        jsonObject.getString(StrictCodes.CHAT_MESSAGE_BODY),
                        jsonObject.getString(StrictCodes.CHAT_MESSAGE_TIME));

                case StrictCodes.STOP_TEST -> {
                    stopTestSession(webSocketSession,jsonObject.getString(StrictCodes.TEST_CODE),
                            jsonObject.getString(StrictCodes.USER_ID));
                }


                default -> {
                    AuditService.addLog(Level.SEVERE,Logs.UNEXPECTED_ERROR + Logs.FUNCTION + "[handleTextMessage] " +
                            StrictCodes.TRANSMISSION_CODE + "[" + jsonObject.getString(StrictCodes.TRANSMISSION_CODE) +
                            "] is not valid");
                    sendErrorResponse(webSocketSession,StrictCodes.TEST_ERROR,StrictCodes.TRANSMISSION_CODE + " [" +
                            jsonObject.getString(StrictCodes.TRANSMISSION_CODE) + "] not good");
                }
            }
        }
        catch (JSONException e) {
            sendErrorResponse(webSocketSession,StrictCodes.TEST_ERROR," payload [" + jsonObject +
                    "] not good");
            e.printStackTrace();
        }
    }



    private TestConnectionData checkConnection(WebSocketSession webSocketSession, String code, boolean sendMessage){
        TestConnectionData connection = connexionsMap.get(code);

        // if connexion does not exist return error
        if(connection == null && sendMessage){
            sendErrorResponse(webSocketSession,StrictCodes.TEST_ERROR,
                    " connection for test code [" + code + "] not found");
            return null;
        }

        return connection;
    }

    private void closeWebSocketConnections(TestConnectionData connection){
        connection.getParticipants().forEach(p -> {
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


    public JSONObject createJsonObjectResponse(HashMap<String,String> payloadValues, JSONArray testQuestions,
                                               JSONArray participants){
        JSONObject payload = new JSONObject();

        // create payload
        payloadValues.forEach((key, value) -> {
            try{
                payload.put(key,value);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        });

        if(testQuestions != null){
            try {
                payload.put(StrictCodes.TEST_QUESTIONS,testQuestions);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(participants != null){
            try {
                payload.put(StrictCodes.PARTICIPANTS,participants);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return payload;
    }

    public void createTestSession(Participant participant, int maxParticipants, JSONArray testQuestions){

        // no test code is available
        if(codesQueue.isEmpty()){
            sendErrorResponse(participant.getWebSocketSession(),StrictCodes.TEST_ERROR,
                    " No code available. Server is full. Test was not created");
            return;
        }

        // get test code
        String testCode = codesQueue.remove();

        // create new connexion
        TestConnectionData testConnectionData = context.getBean(TestConnectionData.class);

        // add connection data
        testConnectionData.setMaxParticipants(maxParticipants);
        testConnectionData.setTestCode(testCode);
        testConnectionData.setTestQuestions(testQuestions);
        testConnectionData.getParticipants().add(participant);

        // add connexion to connexions map
        connexionsMap.put(testCode, testConnectionData);

        // send code
        HashMap<String,String> tmp = new HashMap<>();
        tmp.put(StrictCodes.TRANSMISSION_CODE,StrictCodes.PREPARE_TEST);
        tmp.put(StrictCodes.WEBSOCKET_SESSION_ID, participant.getWebSocketSession().getId());
        tmp.put(StrictCodes.TEST_CODE,testCode);
        tmp.put(StrictCodes.USER_ID,participant.getUserId());
        sendMessage(participant.getWebSocketSession(),createJsonObjectResponse(tmp,testQuestions,null).toString());
    }


    public void joinTestSession(String testCode, Participant participant){

        TestConnectionData connection = checkConnection(participant.getWebSocketSession(), testCode,false);

        if(connection == null){
            sendErrorResponse(participant.getWebSocketSession(), StrictCodes.WRONG_CODE,
                    " connection for test code [" + testCode + "] not found");
            try {
                // disconnect participant
                if(participant.getWebSocketSession().isOpen()){
                    participant.getWebSocketSession().close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // code was good
        // check if participant already joined
        for(Participant p: connection.getParticipants()){

            if(p.getWebSocketSession().isOpen() && participant.getWebSocketSession().isOpen() &&
                    p.getWebSocketSession().getId().equals(participant.getWebSocketSession().getId())){

                sendErrorResponse(p.getWebSocketSession(), StrictCodes.TEST_ERROR,
                        " You already joined joined for test " + testCode);

                return;
            }
        }

        // check if max participants joined
        if(connection.getMaxParticipants() == connection.getParticipants().size()){
            sendErrorResponse(participant.getWebSocketSession(), StrictCodes.TEST_ERROR,
                    " All participant joined for test " + testCode);
            return;
        }

        JSONArray jsonArray = new JSONArray();
        try {
            // construct all participant list
            for (Participant value : connection.getParticipants()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(StrictCodes.PARTICIPANT_IS_ADMIN, value.isTestAdmin())
                        .put(StrictCodes.PARTICIPANT_ID, value.getUserId())
                        .put(StrictCodes.WEBSOCKET_SESSION_ID, value.getWebSocketSession().getId());
                jsonArray.put(jsonObject);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // tell to participant to prepare test
        HashMap<String,String> tmp = new HashMap<>();
        tmp.put(StrictCodes.TRANSMISSION_CODE,StrictCodes.PREPARE_TEST);
        tmp.put(StrictCodes.WEBSOCKET_SESSION_ID, participant.getWebSocketSession().getId());
        tmp.put(StrictCodes.USER_ID,participant.getUserId());
        sendMessage(participant.getWebSocketSession(),
                createJsonObjectResponse(tmp,connection.getTestQuestions(),jsonArray).toString());

        // add participant to connexion
        connection.getParticipants().add(participant);

        // tell everyone (including himself) that a new participant came
        broadcastParticipantJoined(participant,connection);
    }


    public void broadcastStartTest(WebSocketSession webSocketSession, String testCode){

        TestConnectionData connection = checkConnection(webSocketSession, testCode,false);

        if(connection == null){
            sendErrorResponse(webSocketSession, StrictCodes.WRONG_CODE,
                    " connection for test code [" + testCode + "] not found");
            try {
                // disconnect participant
                if(webSocketSession.isOpen()){
                    webSocketSession.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // create common response
        HashMap<String,String> tmp = new HashMap<>();
        tmp.put(StrictCodes.TRANSMISSION_CODE,StrictCodes.START_TEST);

        connection.getParticipants().forEach(p -> {
            tmp.put(StrictCodes.WEBSOCKET_SESSION_ID, p.getWebSocketSession().getId());
            sendMessage(p.getWebSocketSession(),createJsonObjectResponse(tmp,null,null).toString());
        });
    }

    public void broadcastParticipantJoined(Participant participant, TestConnectionData connection){
        // create common response
        HashMap<String,String> tmp = new HashMap<>();
        tmp.put(StrictCodes.TRANSMISSION_CODE,StrictCodes.PARTICIPANT_CONNECTED);
        tmp.put(StrictCodes.PARTICIPANT_IS_ADMIN,String.valueOf(participant.isTestAdmin()));
        tmp.put(StrictCodes.PARTICIPANT_ID,participant.getUserId());

        connection.getParticipants().forEach(p -> {
            tmp.put(StrictCodes.WEBSOCKET_SESSION_ID, p.getWebSocketSession().getId());
            sendMessage(p.getWebSocketSession(),createJsonObjectResponse(tmp,null,null).toString());
        });
    }

    public void broadcastTestChatMessage(WebSocketSession webSocketSession, String testCode, String userId,
                                         String message, String time){

        TestConnectionData connection = checkConnection(webSocketSession,testCode,true);
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
        tmp.put(StrictCodes.TRANSMISSION_CODE,StrictCodes.TEST_CHAT_MESSAGE);
        tmp.put(StrictCodes.WEBSOCKET_SESSION_ID, webSocketSession.getId());

        for(Participant p: connection.getParticipants()){
            tmp.put(StrictCodes.USER_ID,userId);
            tmp.put(StrictCodes.CHAT_MESSAGE_BODY,message);
            tmp.put(StrictCodes.CHAT_MESSAGE_TIME,time);
            sendMessage(p.getWebSocketSession(),createJsonObjectResponse(tmp,null,null).toString());
        }
    }

    public void registerResponse(WebSocketSession webSocketSession, Response response, String userId, String testCode){

        if(response == null){
            return;
        }

        TestConnectionData connection = checkConnection(webSocketSession, testCode,false);

        if(connection == null){
            sendErrorResponse(webSocketSession, StrictCodes.WRONG_CODE,
                    " connection for test code [" + testCode + "] not found");
            try {
                // disconnect participant
                if(webSocketSession.isOpen()){
                    webSocketSession.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        List<Response> responsesList = connection.getResponses().get(userId);
        if (responsesList == null){
            responsesList = new ArrayList<>();
        }
        responsesList.add(response);
        connection.getResponses().put(userId,responsesList);

        // just for test
        HashMap<String,String> tmp = new HashMap<>();
        tmp.put(StrictCodes.TRANSMISSION_CODE,StrictCodes.TEST_RESPONSE);
        tmp.put(StrictCodes.WEBSOCKET_SESSION_ID, webSocketSession.getId());
        tmp.put(StrictCodes.USER_ID,userId);
        tmp.put(StrictCodes.QUESTION_ID,String.valueOf(response.getQuestionId()));
        tmp.put(StrictCodes.QUESTION,response.getQuestion());
        tmp.put(StrictCodes.RESPONSE,response.getResponse());
        tmp.put(StrictCodes.CORRECT_RESPONSE,String.valueOf(response.isCorrectResponse()));
        sendMessage(webSocketSession,createJsonObjectResponse(tmp,null,null).toString());

    }

    public void stopTestSession(WebSocketSession webSocketSession, String testCode, String userId){

        TestConnectionData connection = checkConnection(webSocketSession,testCode,false);

        HashMap<String,String> tmp = new HashMap<>();
        tmp.put(StrictCodes.TRANSMISSION_CODE,StrictCodes.STOP_TEST);

        // if connexion does not exist return error
        if(connection == null){
            tmp.put(StrictCodes.INFO_MESSAGE," connection for test code [" + testCode + "] not found. Test stopped already.");
            sendMessage(webSocketSession,createJsonObjectResponse(tmp,null,null).toString());
        }
        else{
            // broadcast stopping message anc close sessions

            tmp.put(StrictCodes.INFO_MESSAGE," test [" + testCode + "] stopped successfully.");
            tmp.put(StrictCodes.USER_ID,userId);

            for (Participant p: connection.getParticipants()){
                if(p != null) {
                    sendMessage(p.getWebSocketSession(),createJsonObjectResponse(tmp,null,null).toString());
                }
            }

            closeWebSocketConnections(connection);

            // remove connection
            connexionsMap.remove(testCode);
            codesQueue.add(testCode);
        }
    }

}
