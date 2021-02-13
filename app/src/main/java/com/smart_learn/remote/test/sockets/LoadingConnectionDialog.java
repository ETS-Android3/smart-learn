package com.smart_learn.remote.test.sockets;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.TextView;

import com.smart_learn.R;
import com.smart_learn.config.CurrentConfig;
import com.smart_learn.config.GeneralConfig;
import com.smart_learn.data.entities.DictionaryEntrance;
import com.smart_learn.remote.test.config.StrictCodes;
import com.smart_learn.core.services.TestService;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.core.utilities.Logs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;
import okhttp3.WebSocket;

// https://www.youtube.com/watch?v=tccoRIrMyhU
public class LoadingConnectionDialog {

    private LoadingConnectionDialog dialog;
    private AlertDialog loadingDialog;
    private CountDownTimer countDownTimer;

    private TextView tvLoadingMessage;
    private TextView tvInfoMessage;
    private TextView tvTimer;

    public LoadingConnectionDialog(String loadingMessage, String infoMessage) {
        dialog = this;

        // build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(CurrentConfig.getCurrentConfigInstance().currentActivity);
        LayoutInflater inflater = CurrentConfig.getCurrentConfigInstance().currentActivity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_dialog,null));

        // if setCancelable is true when you click beside the dialog the dialog is dismissed
        builder.setCancelable(false);

        loadingDialog = builder.create();
        loadingDialog.show();

        // get View components
        tvLoadingMessage = loadingDialog.findViewById(R.id.tvLoadingMessage);
        tvInfoMessage = loadingDialog.findViewById(R.id.tvInfoMessage);
        tvTimer = loadingDialog.findViewById(R.id.tvTimer);
        Button cancelTest = loadingDialog.findViewById(R.id.btnCancelGeneratedTest);

        // set initial data
        tvLoadingMessage.setText(loadingMessage);
        tvInfoMessage.setText(infoMessage);

        // set listeners
        cancelTest.setOnClickListener(v -> {
            // close dialog and stop countdown timer
            dismissDialog("Test was canceled.", true);

            // stop test
            // mark this true to broadcast stopping message
            TestService.getTestServiceInstance().getAbortTestConnection().set(true);
            TestService.getTestServiceInstance().stopTest();
        });
    }

    private List<DictionaryEntrance> createSampleData(){

        List<DictionaryEntrance> dictionaryEntranceList = new ArrayList<>();
        dictionaryEntranceList.add(new DictionaryEntrance(-1,"hello","salut","", 0));
        dictionaryEntranceList.add(new DictionaryEntrance(-1,"two","2","", 0));
        dictionaryEntranceList.add(new DictionaryEntrance(-1,"three","3","", 0));
        dictionaryEntranceList.add(new DictionaryEntrance(-1,"four","4","", 0));
        dictionaryEntranceList.add(new DictionaryEntrance(-1,"five","5","", 0));

        return dictionaryEntranceList;
    }

    /** helper for creating connection message */
    private String createInitialPayload(boolean generatedTest, String testCode) {

        JSONObject requestBody = new JSONObject();

        try {
            if (generatedTest) {
                requestBody.put(StrictCodes.TRANSMISSION_CODE, StrictCodes.NEW_CONNEXION)
                        .put(StrictCodes.PARTICIPANTS_NUMBER, TestService.getTestServiceInstance().maxParticipants);

                // if participant generated the test send test details
                List<DictionaryEntrance> dictionaryEntranceList = createSampleData();

                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < dictionaryEntranceList.size(); i++){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(StrictCodes.QUESTION_ID,i)
                            .put(StrictCodes.QUESTION,dictionaryEntranceList.get(i).getWord())
                            .put(StrictCodes.RESPONSE,dictionaryEntranceList.get(i).getTranslation());
                    jsonArray.put(jsonObject);
                }

                requestBody.put(StrictCodes.TEST_QUESTIONS, jsonArray);
            }
            else {
                requestBody.put(StrictCodes.TRANSMISSION_CODE, StrictCodes.JOIN_CONNEXION)
                            .put(StrictCodes.TEST_CODE, testCode);
            }

            requestBody.put(StrictCodes.USER_ID, GeneralConfig.USER_ID);
        }
        catch (JSONException e) {
            // mark test as aborted --> it will be stopped in main counter
            TestService.getTestServiceInstance().getAbortTestConnection().set(true);
            e.printStackTrace();
        }

        return requestBody.toString();
    }

    public void startTestConnection(String code, boolean generatedTest){
        waitForConnectionEstablishing();
        initConnection(createInitialPayload(generatedTest,code),generatedTest);
    }

    private void initConnection(String payload, boolean generatedTest){

        // if test was aborted return
        if(TestService.getTestServiceInstance().getAbortTestConnection().get()){
            Log.e(Logs.ERROR, Logs.FUNCTION + "[initConnection] Test aborted ");
            return;
        }

        // if test was not aborted it means that payload was created so continue

        // just make a quick log
        if(generatedTest){
            Log.i(Logs.INFO, Logs.FUNCTION + "[initConnection] Payload [obtain test code]: " + payload);
        }
        else{
            Log.i(Logs.INFO, Logs.FUNCTION + "[initConnection] Payload [join to existing test]: " + payload);
        }


        // Open test web socket connection
        TestService.getTestServiceInstance().setTestWebSocket(
                WebSocketService.getWebSocketServiceInstance()
                        .connectToWebSocket(GeneralConfig.ULR_WEB_SOCKET_CONNECT_TO_TEST, new WebSocketCallback() {
                            @Override
                            public void onOpen(WebSocket webSocket, Response response) {
                                // after socket is opened send payload to server
                                webSocket.send(payload);
                                Log.i(Logs.INFO,Logs.WEB_SOCKET_SENT_MESSAGE + GeneralUtilities.stringToPrettyJson(payload));
                                TestService.getTestServiceInstance().goodWebSocketConn.set(true);
                            }

                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onTextMessage(WebSocket webSocket, String text) {
                                // step on is completed after a confirmation for start test activity
                                // is received
                                if (!TestService.getTestServiceInstance().stepOneCompleted.get()){
                                    TestService.getTestServiceInstance()
                                            .onTextMessageReceived(dialog,webSocket,text);
                                    return;
                                }

                                // call function without dialog
                                TestService.getTestServiceInstance()
                                        .onTextMessageReceived(null,webSocket,text);
                            }

                            @Override
                            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                                Log.e(Logs.UNEXPECTED_ERROR,"On failure web socket [" + webSocket.toString() + "]  " +
                                        "with throwable message [" + t.getMessage() + "] and response " + " [" + response + "]");

                                TestService.getTestServiceInstance().goodWebSocketConn.set(false);

                                // set error code
                                CurrentConfig.getCurrentConfigInstance().requestErrorCode.set(WebSocketService.WEB_SOCKET_REQUEST_ERROR);

                                // abort test
                                TestService.getTestServiceInstance().getAbortTestConnection().set(true);

                            }
                        })
        );

    }

    @SuppressLint("SetTextI18n")
    public void setResponse(int type){

        switch (type){

            // unexpected errors while trying to create test or join test
            case 1:{
                TestService.getTestServiceInstance().getAbortTestConnection().set(true);
                dismissDialog("Unexpected error. Test can not start. Try again later.",true);
                break;
            }

            // participant who wants to join the test sent a wrong code
            case 2: {
                TestService.getTestServiceInstance().getAbortTestConnection().set(true);
                dismissDialog(Logs.UNEXPECTED_ERROR + " Code is not valid",true);
                break;
            }

            default:
                Log.e(Logs.UNEXPECTED_ERROR," Value for message type [" + type + "] is not good");
        }

    }

    /** Main countdown timer which wait until connection is made or aborted */
    private void waitForConnectionEstablishing(){

        // use a countDownTimer for closing the dialog after some time and make operation on background
        // at every onTick moment
        // https://stackoverflow.com/questions/14445745/android-close-dialog-after-5-seconds
        countDownTimer = new CountDownTimer(TestService.CONNECTION_TIMEOUT, 1000) {

            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {

                tvTimer.setText("00:" + millisUntilFinished / 1000);
                Log.i(Logs.INFO, "Main loading dialog countdown timer " + millisUntilFinished / 1000);

                // if connection is canceled close dialog, stop timer and stop test
                if(TestService.getTestServiceInstance().getAbortTestConnection().get()){

                    // if web socket request have an error abort
                    if(CurrentConfig.getCurrentConfigInstance().requestErrorCode.get() == WebSocketService.WEB_SOCKET_REQUEST_ERROR){
                        dismissDialog("Not able to connect to server", true);
                    }

                    // if an unexpected error appears and test was marked as aborted
                    // message will be marked using function setResponse

                    TestService.getTestServiceInstance().stopTest();

                    // dialog will be dismissed if it was not already dismissed
                    // checks are made in dismissDialog function
                    // also this timer will be stopped
                    dismissDialog("",false);
                    return;
                }

                // this means that connexion is established
                if(TestService.getTestServiceInstance().startTestActivity.get()){
                    // close dialog and stop countdown timer
                    dismissDialog("", false);

                    TestService.getTestServiceInstance().launchTestActivity();
                    return;
                }

                // check if websocket connection was made
                if(TestService.getTestServiceInstance().goodWebSocketConn.get()){
                    // websocket connection was made but test activity was not initialized because
                    // is necessary a confirmation from server (websocket listen for messages
                    // and when a confirmation message will be received startTestActivity
                    // will be marked as true)

                    CurrentConfig.getCurrentConfigInstance().currentActivity.runOnUiThread(() -> {
                        tvLoadingMessage.setText("Connection made");
                        tvInfoMessage.setText("Wait for test to start ...");
                    });

                }
            }

            @Override
            public void onFinish() {
                dismissDialog("Connection timeout. Test aborted", true);
                TestService.getTestServiceInstance().stopTest();
            }
        }.start();

    }

    private void dismissDialog(String message, Boolean showMessage){
        if(loadingDialog != null){
            loadingDialog.dismiss();
        }
        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
        if(showMessage) {
            GeneralUtilities.showToast(message);
        }
    }
}


