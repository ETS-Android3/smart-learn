package com.licenta.smart_learn.remote.game.sockets;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.TextView;

import com.licenta.smart_learn.R;
import com.licenta.smart_learn.config.CurrentConfig;
import com.licenta.smart_learn.config.GeneralConfig;
import com.licenta.smart_learn.remote.api.HttpRequestService;
import com.licenta.smart_learn.remote.game.config.StrictCodes;
import com.licenta.smart_learn.services.GameService;
import com.licenta.smart_learn.utilities.GeneralUtilities;
import com.licenta.smart_learn.utilities.Logs;
import com.licenta.smart_learn.utilities.NetworkUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

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

    private AtomicBoolean publicIpTimerFinished = new AtomicBoolean(false);

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
        Button cancelGame = loadingDialog.findViewById(R.id.btnCancelGeneratedGame);

        // set initial data
        tvLoadingMessage.setText(loadingMessage);
        tvInfoMessage.setText(infoMessage);

        // set listeners
        cancelGame.setOnClickListener(v -> {
            // close dialog and stop countdown timer
            dismissDialog("Game was canceled.", true);

            // stop game
            // mark this true to broadcast stopping message
            GameService.getGameServiceInstance().getAbortGameConnection().set(true);
            GameService.getGameServiceInstance().stopGame();
        });
    }

    /** helper for creating connection message */
    private String createInitialPayload(boolean generatedGame, String publicIp, String gameCode) {

        JSONObject requestBody = new JSONObject();

        try {
            if (generatedGame) {
                requestBody.put(StrictCodes.TRANSMISSION_CODE, StrictCodes.NEW_CONNEXION)
                        .put(StrictCodes.PUBLIC_IP, publicIp)
                        .put(StrictCodes.LOCAL_IP, NetworkUtilities.getLocalIPAddress(true))
                        .put(StrictCodes.PLAYERS_NUMBER, GameService.getGameServiceInstance().maxPlayers)
                        .put(StrictCodes.PLAYER_SYMBOL, GameService.getGameServiceInstance().playerSymbol)
                        .put(StrictCodes.FIRST_TO_MOVE, GameService.getGameServiceInstance().canMakeMove.get());

                // FIXME: fix this selection mode for symbol
                if(GameService.getGameServiceInstance().playerSymbol.equals(StrictCodes.PLAYER_A_SYMBOL)){
                    requestBody.put(StrictCodes.OPPONENT_SYMBOL, StrictCodes.PLAYER_B_SYMBOL);
                }
                else{
                    requestBody.put(StrictCodes.OPPONENT_SYMBOL, StrictCodes.PLAYER_A_SYMBOL);
                }

            }
            else {
                requestBody.put(StrictCodes.TRANSMISSION_CODE, StrictCodes.JOIN_CONNEXION)
                        .put(StrictCodes.GAME_CODE, gameCode)
                        .put(StrictCodes.PUBLIC_IP, publicIp)
                        .put(StrictCodes.LOCAL_IP, NetworkUtilities.getLocalIPAddress(true))
                        .put(StrictCodes.PLAYER_SYMBOL, StrictCodes.PLAYER_EMPTY_SYMBOL) // value for this will be set on server
                        .put(StrictCodes.FIRST_TO_MOVE, false); // value for this will be set on server
            }
        }
        catch (JSONException e) {
            // mark game as aborted --> it will be stopped in main counter
            GameService.getGameServiceInstance().getAbortGameConnection().set(true);

            Log.e(Logs.UNEXPECTED_ERROR,Logs.FUNCTION + "[createRequestBody] can not create request body. Game aborted. " + e);
            e.printStackTrace();
        }

        return requestBody.toString();
    }

    public void startGameConnection(String code, boolean generatedGame){
        // find public address
        // This call is an async call.
        NetworkUtilities.findPublicIPAddress();

        // so wait for some seconds to see if a response is received and add public ip into header
        // otherwise ip will be ""
        publicIpTimerFinished.set(false);
        waitForIpRequest(code, generatedGame);

        // and wait until connection is made or canceled
        waitForConnectionEstablishing();
    }

    private void waitForIpRequest(String code, boolean generatedGame){

        new CountDownTimer(10000, 1000) {

            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i(Logs.INFO, "Search ip countdown timer " + millisUntilFinished / 1000);

                // if game is aborted finish
                // game is stopped in main counter
                if(GameService.getGameServiceInstance().getAbortGameConnection().get()){
                    publicIpTimerFinished.set(true);
                    this.cancel();
                    return;
                }

                // if public ip request was done [result(error or success) does not matter because
                // is set on NetworkUtilities.getPublicIpAddress()]
                if(CurrentConfig.getCurrentConfigInstance().requestErrorCode.get() == HttpRequestService.PUBLIC_IP_REQUEST_ERROR ||
                        CurrentConfig.getCurrentConfigInstance().requestErrorCode.get() == HttpRequestService.REQUEST_SUCCESS){

                    publicIpTimerFinished.set(true);

                    String publicIp = NetworkUtilities.getDevicePublicIpAddress().toString();
                    Log.i("Public Ip: ", publicIp);

                    initConnection(createInitialPayload(generatedGame,publicIp,code),generatedGame);

                    this.cancel();
                }

            }

            @Override
            public void onFinish() {
                publicIpTimerFinished.set(true);
                initConnection(createInitialPayload(generatedGame,NetworkUtilities.NO_IP_FOUND,code),generatedGame);
                Log.e(Logs.ERROR, "No public ip obtained");
            }
        }.start();
    }

    private void initConnection(String payload, boolean generatedGame){

        // if game was aborted return
        if(GameService.getGameServiceInstance().getAbortGameConnection().get()){
            Log.e(Logs.ERROR, Logs.FUNCTION + "[initConnection] Game aborted ");
            return;
        }

        // if game was not aborted it means that payload was created so continue

        // just make a quick log
        if(generatedGame){
            Log.i(Logs.INFO, Logs.FUNCTION + "[initConnection] Payload [obtain game code]: " + payload);
        }
        else{
            Log.i(Logs.INFO, Logs.FUNCTION + "[initConnection] Payload [join to existing game]: " + payload);
        }


        // Open game web socket connection
        GameService.getGameServiceInstance().setGameWebSocket(
                WebSocketService.getWebSocketServiceInstance()
                        .connectToWebSocket(GeneralConfig.ULR_WEB_SOCKET_CONNECT_TO_GAME, new WebSocketCallback() {
                            @Override
                            public void onOpen(WebSocket webSocket, Response response) {
                                // after socket is opened send payload to server
                                webSocket.send(payload);
                                Log.i(Logs.INFO,Logs.WEB_SOCKET_SENT_MESSAGE + GeneralUtilities.stringToPrettyJson(payload));
                            }

                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onTextMessage(WebSocket webSocket, String text) {

                                // if code is not received
                                if (!GameService.getGameServiceInstance().stepOneCompleted.get()){
                                    GameService.getGameServiceInstance()
                                            .onTextMessageReceived(dialog,webSocket,text);
                                    return;
                                }

                                // call function without dialog
                                GameService.getGameServiceInstance()
                                        .onTextMessageReceived(null,webSocket,text);

                            }

                            @Override
                            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                                Log.e(Logs.UNEXPECTED_ERROR,"On failure web socket [" + webSocket.toString() + "]  " +
                                        "with throwable message [" + t.getMessage() + "] and response " + " [" + response + "]");

                                // set error code
                                CurrentConfig.getCurrentConfigInstance().requestErrorCode.set(WebSocketService.WEB_SOCKET_REQUEST_ERROR);

                                // abort game
                                GameService.getGameServiceInstance().getAbortGameConnection().set(true);
                            }
                        })
        );


    }

    @SuppressLint("SetTextI18n")
    public void setResponse(int type){

        switch (type){

            // unexpected errors while trying to create game or join game
            case 1:{
                GameService.getGameServiceInstance().getAbortGameConnection().set(true);
                dismissDialog("Unexpected error. No connection was made." +
                        " Try again later.",true);
                break;
            }

            // player who wants to join the game successfully joined
            case 2: {
                CurrentConfig.getCurrentConfigInstance().currentActivity.runOnUiThread(() -> {
                    tvLoadingMessage.setText("Waiting for the other opponent ...");
                    tvInfoMessage.setText("Game code: " + GameService.getGameServiceInstance().gameCode);
                });
                break;
            }

            // player who wants to join the game sent a wrong code
            case 3: {
                GameService.getGameServiceInstance().getAbortGameConnection().set(true);
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
        countDownTimer = new CountDownTimer(GameService.CONNECTION_TIMEOUT, 1000) {

            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {

                tvTimer.setText("00:" + millisUntilFinished / 1000);
                Log.i(Logs.INFO, "Main loading dialog countdown timer " + millisUntilFinished / 1000);

                // If public ip countdown timer not finished return. We proceed after he finishes.
                if(!publicIpTimerFinished.get()){
                    return;
                }

                // if connection is canceled close dialog, stop timer and stop game
                if(GameService.getGameServiceInstance().getAbortGameConnection().get()){

                    // if web socket request have an error abort
                    if(CurrentConfig.getCurrentConfigInstance().requestErrorCode.get() == WebSocketService.WEB_SOCKET_REQUEST_ERROR){
                        dismissDialog("Not able to connect to server", true);
                    }

                    // if an unexpected error appears and game was marked as aborted
                    // message will be marked using function setResponse

                    GameService.getGameServiceInstance().stopGame();

                    // dialog will be dismissed if it was not already dismissed
                    // checks are made in dismissDialog function
                    // also this timer will be stopped
                    dismissDialog("",false);
                    return;
                }


                // check if game connection started
                if(GameService.getGameServiceInstance().initGameConnection.get()){

                    // this means that connexion is established and now players can play
                    if(GameService.getGameServiceInstance().startGameActivity.get()){
                        // close dialog and stop countdown timer
                        dismissDialog("", false);

                        // launch game activity
                        GameService.getGameServiceInstance().launchGameActivity();
                        return;
                    }

                    return;
                }

                // check if opponent joined
                if(GameService.getGameServiceInstance().opponentJoined.get()){
                    // opponent joined but game connection was not initialized --> initialize game connection

                    // mark initialize game connection
                    GameService.getGameServiceInstance().initGameConnection.set(true);

                    // change message in loadingDialog
                    CurrentConfig.getCurrentConfigInstance().currentActivity.runOnUiThread(() -> {
                        tvLoadingMessage.setText("Opponent connected");
                        tvInfoMessage.setText("Make game connexion ...");
                    });

                    // initialize remote game connection only for REMOTE_LAN_PLAY
                    if(GameService.getGameServiceInstance().remotePlayMode.get() != GameService.REMOTE_WEB_SOCKET_PLAY){
                        // connection using web sockets will remain opened because game must be marked as
                        // stopped at final and info transmitted to server

                        // launch connection using classic sockets
                        GameService.getGameServiceInstance().initializeRemoteGameConnection(dialog);
                    }
                    else{
                        // mark that game can start because web socket connection is already established
                        GameService.getGameServiceInstance().startGameActivity.set(true);
                    }
                }
            }

            @Override
            public void onFinish() {
                dismissDialog("Connection timeout. Game aborted", true);
                GameService.getGameServiceInstance().stopGame();
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


