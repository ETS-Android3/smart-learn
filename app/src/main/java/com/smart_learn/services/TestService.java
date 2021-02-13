package com.smart_learn.services;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.smart_learn.presenter.activities.TestActivity;
import com.smart_learn.presenter.activities.TestOnlineActivity;
import com.smart_learn.presenter.activities.ui.test.ChatMessageModel;
import com.smart_learn.presenter.activities.ui.test.ChatMessageRVAdapter;
import com.smart_learn.presenter.activities.ui.test.ParticipantModel;
import com.smart_learn.presenter.activities.ui.test.ParticipantsRVAdapter;
import com.smart_learn.presenter.activities.ui.test.QuestionModel;
import com.smart_learn.config.CurrentConfig;
import com.smart_learn.config.GeneralConfig;
import com.smart_learn.remote.test.BasicPlayMode;
import com.smart_learn.remote.test.RemotePlay;
import com.smart_learn.remote.test.config.StrictCodes;
import com.smart_learn.remote.test.sockets.LoadingConnectionDialog;
import com.smart_learn.remote.test.sockets.WebSocketService;
import com.smart_learn.utilities.GeneralUtilities;
import com.smart_learn.utilities.Logs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.WebSocket;

public final class TestService {

    /** singleton class */
    private static TestService testServiceInstance = null;


    /* ***************************************************************************************
     *                                    INFO CODES
     ****************************************************************************************/
    // test modes
    public static final int NO_TEST_MODE = 0;
    public static final byte REMOTE_MODE_TEST = 1;  // on different devices
    public static final byte LOCAL_MODE_TEST = 2;   // on same device


    /* ****************************************************************************************
     *                            TIMER SETTINGS
     *      - these attributes are used for knowing how much timers can be active
     * ****************************************************************************************/
    /** How much time [in milliseconds] is allocated for connection to be established. */
    public static final int CONNECTION_TIMEOUT = 30000;

    /** How much time [in seconds] is allocated for a participant to choose option. */
    private static final int TURN_TIME = 10;

    /** How much time [in milliseconds] is allocated for the entire test. I use this to close timer
     * automatically after some time if an unexpected error appears. */
    private static final int TOTAL_TEST_TIME = 600000; /* 10 minutes */


    /* ***************************************************************************************
     *                              COMMUNICATION INFO
     * - these attributes are used in remote mode also but are private
     ****************************************************************************************/
    // web socket for test connection
    private WebSocket testWebSocket;

    /** after connection is done mark this to start the test */
    public AtomicBoolean startTestActivity = new AtomicBoolean(false);

    /** step one is to make different calls in onTextMessage() callback function after connection
     * is established*/
    public AtomicBoolean stepOneCompleted = new AtomicBoolean(false);

    /** check if websocket connection is made */
    public AtomicBoolean goodWebSocketConn = new AtomicBoolean(false);


    /** check if test started */
    public AtomicBoolean testStarted = new AtomicBoolean(false);


    /* ****************************************************************************************
                             SETTINGS  FOR  CURRENT  TEST  IN  PROGRESS
              - test in progress can be done in remote mode or local mode

      *****************************************************************************************
                               GENERAL SETTINGS
      ****************************************************************************************/
    /** When test is started this variable will be set with the test activity. */
    public BasicPlayMode basicTestModeActivity = null;

    /** How many participants cant participate in current test */
    public AtomicInteger maxParticipants = new AtomicInteger(0);

    /** which mode was selected (selection is made in TestModeActivity) */
    public AtomicInteger currentTestMode = new AtomicInteger(NO_TEST_MODE);

    /** Use this to avoid to cancel a test multiple times */
    private AtomicBoolean testStopped = new AtomicBoolean(false);

    /** Use to display different layout when test is finished */
    public AtomicBoolean testSuccessfullyFinished = new AtomicBoolean(false);

    /*  ***************************************************************************************
     *                          SETTINGS FOR REMOTE TEST MODE
     *  - attributes bellow are used as public fields for an easy access (eliminating getters
     *    and setters) because I don`t want any validation on setting values and also I want to
     *    have access to the values
     * ****************************************************************************************/

    /* ----------------------------------------------------------------------------------------
     *                              CONNECTION SETTINGS
     -----------------------------------------------------------------------------------------*/

    /** Current test code */
    public StringBuffer testCode = new StringBuffer();

    /** Current websocket session id code */
    public StringBuffer webSocketSessionId = new StringBuffer();

    /** if participant who generated the test wants to abort test or timeout connection occurred */
    private AtomicBoolean abortTestConnection = new AtomicBoolean(false);


    /* ----------------------------------------------------------------------------------------
     *                              PARTICIPANT STATUS SETTINGS
     -----------------------------------------------------------------------------------------*/

    /** if participant generated the test */
    public AtomicBoolean isTestAdmin = new AtomicBoolean(false);

    /** question number , question, response */
    public HashMap<Integer, QuestionModel> testQuestions;

    /** first element will be current question*/
    public Queue<Integer> questionIdQueue;
    public AtomicInteger currentQuestionId = new AtomicInteger();

    public List<ParticipantModel> participantModelList = new ArrayList<>();



    /* BASIC CLASS DEFINITIONS */

    private TestService() {
    }

    public static TestService getTestServiceInstance() {
        if (testServiceInstance == null) {
            testServiceInstance = new TestService();
        }
        return testServiceInstance;
    }


    public void createRemoteConnection() {

        // first check for connection and make initial setup
        /*
        if(NetworkUtilities.notGoodConnection()){
            return;
        }

         */

        // reset remote test info
        resetCurrentTestInfo();

        // current player generates the test so mark that
        isTestAdmin.set(true);

        // show a loading dialog while connecting to test
        LoadingConnectionDialog loadingDialog = new LoadingConnectionDialog("Requesting a test code ...", "");
        // Code does no exists . Will be obtained after request made in dialog.
        loadingDialog.startTestConnection("", isTestAdmin.get());

    }


    public void launchTestActivity() {

        Intent intent;
        switch (currentTestMode.get()) {
            case NO_TEST_MODE:
                GeneralUtilities.showToast(Logs.ERROR + "No test mode is selected");
                return;
            case LOCAL_MODE_TEST:
                intent = new Intent(CurrentConfig.getCurrentConfigInstance().currentContext, TestActivity.class);
                break;
            case REMOTE_MODE_TEST:
                intent = new Intent(CurrentConfig.getCurrentConfigInstance().currentContext, TestOnlineActivity.class);
                break;
            default:
                GeneralUtilities.showToast(Logs.UNEXPECTED_ERROR + "test was not launched");
                return;
        }
        CurrentConfig.getCurrentConfigInstance().currentContext.startActivity(intent);
    }


    private void setDialogResponse(LoadingConnectionDialog dialog, int responseType) {
        if (dialog != null) {
            dialog.setResponse(responseType);
        }
    }


    /**
     * When a response is receive on test remote play mode WebSocket here response will be parsed
     *
     * New function which will handle every receive message in a test connection
     */
    public void onTextMessageReceived(LoadingConnectionDialog dialog, WebSocket webSocket, String text) {
        // try extract payload
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(text);
        }
        catch (JSONException e) {
            e.printStackTrace();

            // if test is not launched set a toast message for user
            // test will be stopped in dialog counter
            if(!startTestActivity.get()){
                setDialogResponse(dialog, 1);
                return;
            }

            // otherwise mark to stop test
            abortTestConnection.set(true);
            return;
        }

        // payload was extracted so parse it
        try {

            switch (jsonObject.getString(StrictCodes.TRANSMISSION_CODE)) {

                // for unexpected error
                case StrictCodes.TEST_ERROR: {
                    // if test is not launched set a toast message for user
                    // test will be stopped in dialog counter
                    if(!startTestActivity.get()){
                        setDialogResponse(dialog, 1);
                        return;
                    }

                    // otherwise mark to stop test
                    abortTestConnection.set(true);
                    break;
                }

                // Only for player who wants to join to an existing test.
                case StrictCodes.WRONG_CODE: {
                    // test will be stopped in dialog counter
                    setDialogResponse(dialog,3);
                    break;
                }

                // this means that connection is established
                case StrictCodes.PREPARE_TEST: {
                    // set step one as completed (used by participants who join the test)
                    stepOneCompleted.set(true);

                    if(isTestAdmin.get()){

                        // code is retrieved so delete previous code
                        testCode.delete(0, TestService.getTestServiceInstance().testCode.length());
                        // and add new code
                        testCode.append(jsonObject.getString(StrictCodes.TEST_CODE));

                        // set himself as participant
                        participantModelList = new ArrayList<>();
                        participantModelList.add(new ParticipantModel(true,GeneralConfig.USER_ID,
                                    ParticipantsRVAdapter.VIEW_TYPE_CONNECTED));

                    }

                    // get websocket session id
                    // delete previous data
                    webSocketSessionId.delete(0, webSocketSessionId.length());
                    // add new data
                    webSocketSessionId.append(jsonObject.getString(StrictCodes.WEBSOCKET_SESSION_ID));

                    // set test questions
                    testQuestions = new HashMap<>();
                    questionIdQueue = new PriorityQueue<>();
                    List<Integer> idList = new ArrayList<>();
                    JSONArray jsonArray = jsonObject.getJSONArray(StrictCodes.TEST_QUESTIONS);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject tmp = jsonArray.getJSONObject(i);
                        int id = tmp.getInt(StrictCodes.QUESTION_ID);
                        idList.add(id);
                        String question = tmp.getString(StrictCodes.QUESTION);
                        String answer = tmp.getString(StrictCodes.RESPONSE);

                        TestService.getTestServiceInstance().testQuestions.put(id, new QuestionModel(question,answer));
                    }

                    // TODO: set random question queue
                    questionIdQueue.addAll(idList);

                    // set participants
                    if(!isTestAdmin.get()){
                        jsonArray = jsonObject.getJSONArray(StrictCodes.PARTICIPANTS);
                        participantModelList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject tmp = jsonArray.getJSONObject(i);
                            participantModelList.add(
                                    new ParticipantModel(Boolean.parseBoolean(tmp.getString(StrictCodes.PARTICIPANT_IS_ADMIN)),
                                            tmp.getString(StrictCodes.PARTICIPANT_ID), ParticipantsRVAdapter.VIEW_TYPE_CONNECTED));
                        }
                    }

                    // set that test activity can start
                    startTestActivity.set(true);

                    break;
                }

                case StrictCodes.START_TEST: {

                    if(testStarted.get()){
                        return;
                    }

                    testStarted.set(true);
                    break;
                }

                // used after test started
                case StrictCodes.TEST_RESPONSE: {

                    // if this transmission have the same websocket session id ignore it
                    // because it was resent to current player
                    if(webSocketSessionId.toString().equals(jsonObject.getString(StrictCodes.WEBSOCKET_SESSION_ID))){
                        Log.i(Logs.INFO,"Transmission ignored");
                        return;
                    }


                    // update gui
                    //((RemotePlay)basicPlayModeActivity).updateTable();

                    // this is used to notify fragment tab
                    //((RemotePlay)basicPlayModeActivity).notifyReceivedMove();

                    break;
                }

                // used after test started
                case StrictCodes.TEST_CHAT_MESSAGE: {

                    // if this transmission have the same websocket session id ignore it
                    // because it was resent to current player
                    if(webSocketSessionId.toString().equals(jsonObject.getString(StrictCodes.WEBSOCKET_SESSION_ID))){
                        Log.i(Logs.INFO,"Transmission ignored");
                        return;
                    }

                    // build message
                    ChatMessageModel chatMessageModel = new ChatMessageModel(
                            webSocketSessionId.toString().substring(0,5),
                            jsonObject.getString(StrictCodes.CHAT_MESSAGE_BODY),
                            jsonObject.getString(StrictCodes.CHAT_MESSAGE_TIME),
                            ChatMessageRVAdapter.VIEW_TYPE_MESSAGE_RECEIVED
                    );

                    // add message to recycler view
                    ((RemotePlay) basicTestModeActivity).addChatMessage(chatMessageModel);

                    break;
                }

                case StrictCodes.PARTICIPANT_CONNECTED: {

                    // build message
                    ParticipantModel participantModel = new ParticipantModel(
                            Boolean.parseBoolean(jsonObject.getString(StrictCodes.PARTICIPANT_IS_ADMIN)),
                            jsonObject.getString(StrictCodes.PARTICIPANT_ID),
                            ParticipantsRVAdapter.VIEW_TYPE_CONNECTED
                    );

                    // add message to recycler view
                    if(basicTestModeActivity == null){
                        participantModelList.add(participantModel);
                    }
                    else{
                        ((RemotePlay) basicTestModeActivity).addParticipant(participantModel);
                    }
                    break;
                }

                // confirmation received for stopping test
                case StrictCodes.STOP_TEST: {
                    // mark these false in order to skip sending STOP_TEST to server
                    abortTestConnection.set(false);
                    stopTest();
                    break;
                }

                default: {
                    Log.e(Logs.UNEXPECTED_ERROR, Logs.FUNCTION + "[onTextMessageReceived] " +
                            StrictCodes.TRANSMISSION_CODE + " [" + jsonObject.getString(StrictCodes.TRANSMISSION_CODE) + "] is not valid " +
                            "for payload " + GeneralUtilities.stringToPrettyJson(text));
                    setDialogResponse(dialog, 1);
                    break;
                }
            }
        } catch (JSONException e) {
            setDialogResponse(dialog, 1);
            e.printStackTrace();
        }
    }

    public void sendChatMessage(ChatMessageModel chatMessageModel){

        Thread thread = new Thread() {
            @Override
            public void run() {
                Log.i(Logs.INFO,"Thread [" + this.getName() + "] started");

                JSONObject payload = new JSONObject();
                try {
                    // create payload
                    payload.put(StrictCodes.TRANSMISSION_CODE, StrictCodes.TEST_CHAT_MESSAGE)
                            .put(StrictCodes.TEST_CODE, testCode)
                            .put(StrictCodes.WEBSOCKET_SESSION_ID, webSocketSessionId.toString())
                            .put(StrictCodes.USER_ID, chatMessageModel.getUserId())
                            .put(StrictCodes.CHAT_MESSAGE_BODY, chatMessageModel.getText())
                            .put(StrictCodes.CHAT_MESSAGE_TIME, chatMessageModel.getTime());

                    testWebSocket.send(payload.toString());
                    Log.i(Logs.INFO, "[WebSocket - send] " + GeneralUtilities.stringToPrettyJson(payload.toString()));

                }
                catch (JSONException e) {
                    e.printStackTrace();
                    GeneralUtilities.showToast(Logs.UNEXPECTED_ERROR + "Chat message cannot be sent to server. Try again.");
                }
                finally {
                    Log.i(Logs.INFO,"Thread [" + this.getName() + "] finished");
                }
            }
        };
        thread.setName("THREAD_SEND_CHAT_MESSAGE");
        thread.start();
    }

    // FIXME: this function can be called when a START_TEST payload is receive but an error occurred
    //  websocket is closed on failure because I try to launch a countdown timer. I believe is because
    //  launh is made from websocket thread probably
    public void startOnlineTest(){

        // how much time is allocated for a turn (in seconds)
        AtomicInteger turnTime = new AtomicInteger(TURN_TIME);
        AtomicBoolean firstQuestion = new AtomicBoolean(true);

        // test must be finished in TOTAL_TEST_TIME
        new CountDownTimer(TOTAL_TEST_TIME, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                //Log.i(Logs.INFO,"Time [MAIN_TEST_TIME] " + millisUntilFinished / 1000);

                if(abortTestConnection.get()){
                    ((RemotePlay) basicTestModeActivity).disableTest(false,Logs.UNEXPECTED_ERROR + "Test aborted");
                    this.cancel();
                }

                if(!testStarted.get()){
                    return;
                }

                int time = turnTime.decrementAndGet();
                ((RemotePlay) basicTestModeActivity).updateTime(String.valueOf(time));

                // time for choosing is ended up --> go to next question
                if(time <= 0 || firstQuestion.get()){
                    firstQuestion.set(false);

                    if(questionIdQueue == null || questionIdQueue.isEmpty()){
                        int totalQuestions = 0;
                        int correctResponses = 0;
                        // get a small statistics
                        for (QuestionModel value : testQuestions.values()) {
                            totalQuestions++;
                            if(value.isCorrectResponse()){
                                correctResponses++;
                            }
                        }

                        ((RemotePlay) basicTestModeActivity).disableTest(false,
                                "Test finished: " + correctResponses + "/" + totalQuestions);
                        testSuccessfullyFinished.set(true);

                        this.cancel();
                        return;
                    }

                    currentQuestionId.set(questionIdQueue.remove());
                    QuestionModel questionModel = testQuestions.get(currentQuestionId.get());
                    if(questionModel == null){
                        GeneralUtilities.showToast(Logs.UNEXPECTED_ERROR + " no question model to show");
                        ((RemotePlay) basicTestModeActivity).disableTest(false,Logs.UNEXPECTED_ERROR + "Test aborted");
                        this.cancel();
                        return;
                    }

                    ((RemotePlay) basicTestModeActivity).setNextQuestion(questionModel.getQuestion());

                    turnTime.set(TURN_TIME);

                }

            }

            @Override
            public void onFinish() {
                // testTimer will run for a maximum time, so in eventuality that an unexpected error
                // appears and this timer is not stopped when it should be using conditions from
                // onTick function then he will be stopped when count down is over

                if(basicTestModeActivity != null) {
                    ((RemotePlay) basicTestModeActivity).updateTime("00");
                    ((RemotePlay) basicTestModeActivity).disableTest(false, Logs.UNEXPECTED_ERROR +
                            "Test was active for to much time. Test disabled.");
                }
                Log.e(Logs.UNEXPECTED_ERROR,"Timer [MAIN_TEST_TIMER] finished because test count down was over.");

            }
        }.start();


    }

    public void stopTest(){
        // test was already stopped
        if(testStopped.get()){
            Log.e(Logs.UNEXPECTED_ERROR,"Test was already stopped.");
            return;
        }

        Log.i(Logs.INFO,"Stopping test.");


        /* Make request here because when resetCurrentTestInfo() is called test code will be deleted.

        Send request to notify server to abort test and delete existing connexion info. */

        /*
        // only an admin can stop the test
        if(isTestAdmin.get()){
            JSONObject payload = new JSONObject();
            try {
                // create payload
                payload.put(StrictCodes.TRANSMISSION_CODE, StrictCodes.STOP_TEST)
                        .put(StrictCodes.TEST_CODE, TestService.getTestServiceInstance().testCode)
                        .put(StrictCodes.USER_ID, GeneralConfig.USER_ID);

                // send message
                testWebSocket.send(payload.toString());
                Log.i(Logs.INFO, Logs.WEB_SOCKET_SENT_MESSAGE + GeneralUtilities.stringToPrettyJson(payload.toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }

         */


        if(testWebSocket != null){
            testWebSocket.close(WebSocketService.getNormalClosureStatus(),"Test stopped");
        }

        resetCurrentTestInfo();

        // mark test is stopped because he will be reset in resetCurrentTestInfo()
        // this will stop everything related to current test connection
        abortTestConnection.set(true);

        // mark test is stopped because he will be reset in resetCurrentTestInfo()
        // this will prevent to stop test a second time
        testStopped.set(true);

        Log.i(Logs.INFO,"Test stopped");
    }

    public void resetCurrentTestInfo() {

        abortTestConnection.set(false);
        startTestActivity.set(false);
        testStopped.set(false);
        stepOneCompleted.set(false);
        goodWebSocketConn.set(false);

        testCode.delete(0, testCode.length());
        isTestAdmin.set(false);
        basicTestModeActivity = null;
    }

    public void sendStartTestPayload(){

        JSONObject payload = new JSONObject();
        try {
            payload.put(StrictCodes.TRANSMISSION_CODE, StrictCodes.START_TEST)
                    .put(StrictCodes.TEST_CODE, testCode)
                    .put(StrictCodes.WEBSOCKET_SESSION_ID, webSocketSessionId.toString());

            testWebSocket.send(payload.toString());
            Log.i(Logs.INFO, payload.toString());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String createResponsePayload(int questionId, String response){
        JSONObject payload = new JSONObject();
        try {
            QuestionModel questionModel = testQuestions.get(questionId);
            if(questionModel == null) {
                return null;
            }

            // first attach participant response to question and update hash
            questionModel.setParticipantResponse(response);
            testQuestions.put(questionId,questionModel);

            payload.put(StrictCodes.TRANSMISSION_CODE, StrictCodes.TEST_RESPONSE)
                    .put(StrictCodes.TEST_CODE, testCode)
                    .put(StrictCodes.WEBSOCKET_SESSION_ID, webSocketSessionId.toString())
                    .put(StrictCodes.USER_ID, GeneralConfig.USER_ID)
                    .put(StrictCodes.QUESTION_ID, currentQuestionId.get())
                    .put(StrictCodes.QUESTION, questionModel.getQuestion())
                    .put(StrictCodes.RESPONSE, questionModel.getParticipantResponse())
                    .put(StrictCodes.CORRECT_RESPONSE, questionModel.isCorrectResponse());

        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return payload.toString();
    }

    public void webSocketSendResponse(int questionId, String response){

        String payload = createResponsePayload(questionId, response);

        if(payload == null){
            GeneralUtilities.showToast(Logs.UNEXPECTED_ERROR + "Response cannot be send to server. Try again.");
            return;
        }

        testWebSocket.send(payload);
        Log.i(Logs.INFO, payload);
    }


    public void abortTestAlert(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("You want to leave?");
        builder.setCancelable(false);

        builder.setPositiveButton(
                "Yes",
                (dialog, id) -> {
                    dialog.cancel();
                    ((RemotePlay)context).disableTest(true,"");
                });

        builder.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }

    public AtomicBoolean getAbortTestConnection() {
        return abortTestConnection;
    }

    public AtomicBoolean getTestStopped() {
        return testStopped;
    }

    public void setTestWebSocket(WebSocket testWebSocket) {
        this.testWebSocket = testWebSocket;
    }


}

