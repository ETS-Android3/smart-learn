package com.licenta.smart_learn.services;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.licenta.smart_learn.activities.GameAActivity;
import com.licenta.smart_learn.activities.ui.game_a.ChatMessageModel;
import com.licenta.smart_learn.activities.ui.game_a.ChatMessageRVAdapter;
import com.licenta.smart_learn.config.CurrentConfig;
import com.licenta.smart_learn.config.GeneralConfig;
import com.licenta.smart_learn.remote.game.BasicPlayMode;
import com.licenta.smart_learn.remote.game.RemotePlay;
import com.licenta.smart_learn.remote.game.config.StrictCodes;
import com.licenta.smart_learn.remote.game.sockets.LoadingConnectionDialog;
import com.licenta.smart_learn.remote.game.sockets.WebSocketService;
import com.licenta.smart_learn.utilities.GeneralUtilities;
import com.licenta.smart_learn.utilities.Logs;
import com.licenta.smart_learn.utilities.NetworkUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.WebSocket;

public final class GameService {

    /** singleton class */
    private static GameService gameServiceInstance = null;


    /* ***************************************************************************************
     * INFO CODES
     ****************************************************************************************/
    // game types
    public static final byte NO_GAME = 0;
    public static final byte GAME_A = 1;

    // game modes
    public static final int NO_PLAY_MODE = 2;
    public static final byte REMOTE_MODE_PLAY = 3;  // on different devices
    public static final byte LOCAL_MODE_PLAY = 4;   // on same device
    public static final byte CPU_MODE_PLAY = 5;     // on same device

    // remote game modes
    public static final byte NO_REMOTE_MODE = 6;
    public static final byte REMOTE_LAN_PLAY = 7;  // on same network
    public static final byte REMOTE_WEB_SOCKET_PLAY = 9;  // does not matter if is LAN or WAN network


    /* ****************************************************************************************
     *                            TIMER SETTINGS
     *      - these attributes are used for knowing how much timers can be active
     * ****************************************************************************************/
    /** How much time [in milliseconds] is allocated for connection to be established. */
    public static final int CONNECTION_TIMEOUT = 30000;

    /** How much time [in seconds] is allocated for a player to move. */
    private static final int TURN_TIME = 60;

    /** How much time [in milliseconds] is allocated for the entire game. I use this to close timer
     * automatically after some time if an unexpected error appears. */
    private static final int TOTAL_GAME_TIME = 600000; /* 10 minutes */

    /** controls the flow of game timer */
    private AtomicBoolean resetGameTimer = new AtomicBoolean(false);


    /* ***************************************************************************************
     *                              COMMUNICATION INFO
     * - these attributes are used in remote mode also but are private
     ****************************************************************************************/
    // web socket for game connection
    private WebSocket gameWebSocket;

    // server which is created on this device when players are on the same LAN network
    private ServerSocket localServerSocket;
    private Socket clientSocket;

    // streams associated for communication using classic sockets
    private DataOutputStream output;
    private DataInputStream input;

    // stop socket thread when player is waiting for the opponent move
    private AtomicBoolean stopSocketThread = new AtomicBoolean(false);

    // thread started when player is waiting for the opponent move using sockets
    private Thread socketThread;

    /** after connection is done mark this to start the game */
    public AtomicBoolean startGameActivity = new AtomicBoolean(false);


    /* ****************************************************************************************
                             SETTINGS  FOR  CURRENT  GAME  IN  PROGRESS
              - game in progress can be played in remote mode, local mode or cpu mode

      *****************************************************************************************
                               GENERAL SETTINGS
      ****************************************************************************************/
    /** When game is started this variable will be set with the game activity. */
    private BasicPlayMode basicPlayModeActivity = null;

    /** which game was selected (selection is made in the specific game activity) */
    public byte currentGame = NO_GAME;

    /** How many player cant participate in current game */
    public AtomicInteger maxPlayers = new AtomicInteger(0);

    /** which mode was selected (selection is made in GameModeActivity) */
    public AtomicInteger currentPlayMode = new AtomicInteger(NO_PLAY_MODE);

    /** Use this to avoid to cancel a game multiple times */
    private AtomicBoolean gameStopped = new AtomicBoolean(false);

    /*  ***************************************************************************************
     *                          SETTINGS FOR REMOTE PLAY MODE
     *  - attributes bellow are used as public fields for an easy access (eliminating getters
     *    and setters) because I don`t want any validation on setting values and also I want to
     *    have access to the values
     * ****************************************************************************************/

    /* ----------------------------------------------------------------------------------------
     *                              CONNECTION SETTINGS
     -----------------------------------------------------------------------------------------*/

    /** remote modes can be LAN, WAN or WEB_SOCKETS */
    public AtomicInteger remotePlayMode = new AtomicInteger(NO_REMOTE_MODE);

    /** Information about opponent server if connection will be on same LAN network
     * and the current player generated the game (who generated the game is a client and he will
     * be connecting to the opponent server. opponent port is the device default port. */
    public StringBuffer opponentLocalIp = new StringBuffer();

    /** Current game code */
    public StringBuffer gameCode = new StringBuffer();

    /** Current websocket session id code */
    public StringBuffer webSocketSessionId = new StringBuffer();

    /** if first step of connexion is established */
    public AtomicBoolean opponentJoined = new AtomicBoolean(false);

    /** if opponent joined will start start 2`nd step (create game connection) */
    public AtomicBoolean initGameConnection = new AtomicBoolean(false);

    /** if player wants to abort game or timeout connection occurred */
    private AtomicBoolean abortGameConnection = new AtomicBoolean(false);


    /* ----------------------------------------------------------------------------------------
     *                              PLAYER STATUS SETTINGS
     -----------------------------------------------------------------------------------------*/

    /** if player generated the game */
    public AtomicBoolean playerGenerateGame = new AtomicBoolean(false);

    public AtomicBoolean canMakeMove = new AtomicBoolean(false);
    public String playerSymbol = "";

    public AtomicBoolean webSocketMoveReceived = new AtomicBoolean(false);

    /** step one is to make different calls in onTextMessage() callback function after connection
     * is established*/
    public AtomicBoolean stepOneCompleted = new AtomicBoolean(false);

    /** position where opponent moved */
    public AtomicInteger opponentMovePosition = new AtomicInteger(-1);



    /*  ***************************************************************************************
     *                          SETTINGS FOR REMOTE PLAY MODE
     * ****************************************************************************************/
    public AtomicInteger cpuDifficulty = new AtomicInteger(1);


    /* BASIC CLASS DEFINITIONS */

    private GameService() {
    }

    public static GameService getGameServiceInstance() {
        if (gameServiceInstance == null) {
            gameServiceInstance = new GameService();
        }
        return gameServiceInstance;
    }

    /**
     * This function is called only in local mode play
     *
     * @param symbol  refers to playerSymbol
     * @param canMove refers to firstPlayerToMove
     */
    public void createLocalCPUGame(String symbol, boolean canMove, int difficulty) {

        // reset remote game info
        resetCurrentGameInfo();

        // mark settings
        canMakeMove.set(canMove);
        playerSymbol = symbol;
        cpuDifficulty.set(difficulty);

        launchGameActivity();
    }


    /**
     * This function is called only in remote mode play
     *
     * @param symbol  refers to playerSymbol
     * @param canMove refers to firstPlayerToMove
     */
    public void createRemoteHostConnection(String symbol, boolean canMove) {

        // first check for connection and make initial setup
        /*
        if(NetworkUtilities.notGoodConnection()){
            return;
        }

         */

        // reset remote game info
        resetCurrentGameInfo();

        // current player generates the game so mark that
        playerGenerateGame.set(true);

        // mark player settings
        canMakeMove.set(canMove);
        playerSymbol = symbol;

        // show a loading dialog while connecting to game
        LoadingConnectionDialog loadingDialog = new LoadingConnectionDialog("Requesting a game code ...", "");
        // Code does no exists . Will be obtained after request made in dialog.
        loadingDialog.startGameConnection("", playerGenerateGame.get());

    }

    public void initializeRemoteGameConnection(LoadingConnectionDialog dialog) {
        Log.i(Logs.INFO, "[Remote play mode] " + remotePlayMode.get());

        // check remote mode type
        switch (remotePlayMode.get()) {
            case NO_REMOTE_MODE:
                GeneralUtilities.showToast(Logs.ERROR + "No remote mode is selected");
                // game will be stopped in timer from loading dialog
                dialog.setResponse(1);
                break;
            case REMOTE_LAN_PLAY:
                if (playerGenerateGame.get()) {
                    startSocketServer();
                } else {
                    connectToSocketServer();
                }
                break;
            case REMOTE_WEB_SOCKET_PLAY:
                GeneralUtilities.notImplemented();
                break;
            default:
                GeneralUtilities.showToast(Logs.UNEXPECTED_ERROR + "No existing remote mode for play");
                // game will be stopped in timer from loading dialog
                dialog.setResponse(1);
        }

    }

    public void launchGameActivity() {

        Intent intent;
        switch (currentGame) {
            case NO_GAME:
                GeneralUtilities.showToast(Logs.ERROR + "No game is selected");
                return;
            case GAME_A:
                intent = new Intent(CurrentConfig.getCurrentConfigInstance().currentContext, GameAActivity.class);
                break;
            default:
                GeneralUtilities.showToast(Logs.UNEXPECTED_ERROR + "game was not launched");
                return;
        }
        CurrentConfig.getCurrentConfigInstance().currentContext.startActivity(intent);
    }

    public void selectConnectionFunction(int position, boolean selectListener, boolean playerCanMove){

        // if player can not make the first move then choose the right function to wait for the
        // opponent move based on selected play mode
        Log.i(Logs.INFO, "[Play mode]  " + currentPlayMode.get());
        switch(currentPlayMode.get()){
            case NO_PLAY_MODE:
                ((RemotePlay)basicPlayModeActivity).disableGame(false,Logs.UNEXPECTED_ERROR +
                        "No play mode is selected. Game disabled.");
                return;
            case LOCAL_MODE_PLAY:
            case CPU_MODE_PLAY:
                GeneralUtilities.notImplemented();
                break;
            case REMOTE_MODE_PLAY: {
                Log.i(Logs.INFO, "[Play mode] REMOTE_MODE_PLAY = [" + remotePlayMode.get() + "]");

                // check remote mode type
                switch(remotePlayMode.get()){
                    case NO_REMOTE_MODE:
                        ((RemotePlay)basicPlayModeActivity).disableGame(false,Logs.UNEXPECTED_ERROR +
                                "No remote mode is selected. Game disabled.");
                        break;
                    case REMOTE_LAN_PLAY:
                        if(selectListener && playerCanMove) {
                            socketsMakeYourMove(position);
                            return;
                        }
                        if(!selectListener && !playerCanMove) {
                            socketsWaitForTheOtherPlayerMove();
                            return;
                        }
                        GeneralUtilities.showToast(Logs.UNEXPECTED_ERROR + "Unable to select play mode function");
                        break;
                    case REMOTE_WEB_SOCKET_PLAY:
                        if(selectListener && playerCanMove) {
                            webSocketMakeYourMove(position);
                            return;
                        }
                        if(!selectListener && !playerCanMove) {
                            webSocketWaitForTheOtherPlayerMove();
                            return;
                        }
                        GeneralUtilities.showToast(Logs.UNEXPECTED_ERROR + "Unable to select play mode function");
                        break;
                    default:
                        ((RemotePlay)basicPlayModeActivity).disableGame(false,Logs.UNEXPECTED_ERROR +
                                "No existing remote mode for play. Game disabled.");
                }

                break;
            }
            default:
                ((RemotePlay)basicPlayModeActivity).disableGame(false,Logs.UNEXPECTED_ERROR +
                        "No existing play mode type. Game disabled.");
        }
    }

    private void setDialogResponse(LoadingConnectionDialog dialog, int responseType) {
        if (dialog != null) {
            dialog.setResponse(responseType);
        }
    }

    /**
     * When a response is receive on game remote play mode WebSocket here response will be parsed
     *
     * New function which will handle every receive message in a game connection
     */
    public void onTextMessageReceived(LoadingConnectionDialog dialog, WebSocket webSocket, String text) {
        // try extract payload
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(text);
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.e(Logs.ERROR, Logs.FUNCTION + "[onTextMessageReceived] " +
                    "Failed to parse json" + GeneralUtilities.stringToPrettyJson(text));

            // if game is not launched set a toast message for user
            // game will be stopped in dialog counter
            if(!startGameActivity.get()){
                setDialogResponse(dialog, 1);
                return;
            }

            // otherwise mark to stop game
            abortGameConnection.set(true);
            return;
        }

        // payload was extracted so parse it
        try {

            switch (jsonObject.getString(StrictCodes.TRANSMISSION_CODE)) {

                // for unexpected error
                case StrictCodes.GAME_ERROR: {
                    // if game is not launched set a toast message for user
                    // game will be stopped in dialog counter
                    if(!startGameActivity.get()){
                        setDialogResponse(dialog, 1);
                        return;
                    }

                    // otherwise mark to stop game
                    abortGameConnection.set(true);
                    break;
                }

                // Only for player who generates the game.
                case StrictCodes.GAME_CODE_RECEIVED: {
                    // code is retrieved so delete previous code
                    gameCode.delete(0, GameService.getGameServiceInstance().gameCode.length());
                    // and add new code
                    gameCode.append(jsonObject.getString(StrictCodes.GAME_CODE));

                    // set step one as completed
                    stepOneCompleted.set(true);

                    // show new dialog message
                    setDialogResponse(dialog, 2);
                    break;
                }

                // Only for player who wants to join to an existing game.
                case StrictCodes.WRONG_CODE: {
                    // game will be stopped in dialog counter
                    setDialogResponse(dialog,3);
                    break;
                }

                // this means that connection is established
                case StrictCodes.START_GAME: {
                    // set step one as completed (used by opponent who joins the game)
                    stepOneCompleted.set(true);

                    // opponent joined
                    opponentJoined.set(true);

                    // opponent is joined --> get game info

                    // if play is on same network get the other info about connection
                    if(Boolean.parseBoolean(jsonObject.getString(StrictCodes.LOCALHOST))){

                        remotePlayMode.set(REMOTE_LAN_PLAY);

                        // get opponent local ip
                        if(!playerGenerateGame.get()){
                            // delete previous data
                            opponentLocalIp.delete(0, opponentLocalIp.length());

                            // add new data
                            opponentLocalIp.append(jsonObject.getString(StrictCodes.LOCAL_IP));
                        }
                    }
                    else{
                        remotePlayMode.set(REMOTE_WEB_SOCKET_PLAY);
                    }

                    // if player did not generate game then get game info
                    if(!playerGenerateGame.get()){
                        playerSymbol = jsonObject.getString(StrictCodes.PLAYER_SYMBOL);
                        canMakeMove.set(Boolean.parseBoolean(jsonObject.getString(StrictCodes.FIRST_TO_MOVE)));
                    }

                    // get websocket session id
                    // delete previous data
                    webSocketSessionId.delete(0, webSocketSessionId.length());
                    // add new data
                    webSocketSessionId.append(jsonObject.getString(StrictCodes.WEBSOCKET_SESSION_ID));

                    break;
                }

                // used after game started
                case StrictCodes.GAME_MOVE: {

                    // if this transmission have the same websocket session id ignore it
                    // because it was resent to current player
                    if(webSocketSessionId.toString().equals(jsonObject.getString(StrictCodes.WEBSOCKET_SESSION_ID))){
                        Log.i(Logs.INFO,"Transmission ignored");
                        return;
                    }

                    // get position
                    try{
                        opponentMovePosition.set(Integer.parseInt(jsonObject.getString(StrictCodes.PLAYER_MOVE)));
                    }
                    catch (NumberFormatException ex){
                        ((RemotePlay)basicPlayModeActivity).disableGame(false,Logs.UNEXPECTED_ERROR +
                                " invalid position [" + jsonObject.getString(StrictCodes.PLAYER_MOVE) +
                                "] received. Game disabled.");
                    }

                    // notify web socket wai for move timer that move was received
                    // will have effect only when remote play mode is WEB_SOCKET
                    webSocketMoveReceived.set(true);

                    // stop current thread for socket wait for move
                    // will have effect only when remote play mode is SOCKET
                    stopSocketThread.set(true);

                    // mark that player can move
                    canMakeMove.set(true);

                    // notify game timer that opponent made his move
                    resetGameTimer.set(true);

                    // update gui
                    ((RemotePlay)basicPlayModeActivity).updateTable(((RemotePlay)basicPlayModeActivity).getSymbol(true),
                            GameService.getGameServiceInstance().opponentMovePosition.get(),Color.RED);

                    // this is used to notify fragment tab
                    ((RemotePlay)basicPlayModeActivity).notifyReceivedMove();

                    break;
                }

                // used after game started
                case StrictCodes.GAME_CHAT_MESSAGE: {

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
                    ((RemotePlay)basicPlayModeActivity).addChatMessage(chatMessageModel);

                    break;
                }

                // confirmation received for stopping game or other player stopped game
                case StrictCodes.STOP_GAME: {
                    // mark these false in order to skip sending STOP_GAME to server
                    abortGameConnection.set(false);
                    stopGame();
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
            Log.e(Logs.UNEXPECTED_ERROR, Logs.FUNCTION + "[onTextMessageReceived] Failed to Parse Json ["
                    + jsonObject + "] " + e);
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
                    payload.put(StrictCodes.TRANSMISSION_CODE, StrictCodes.GAME_CHAT_MESSAGE)
                            .put(StrictCodes.GAME_CODE, gameCode)
                            .put(StrictCodes.WEBSOCKET_SESSION_ID, webSocketSessionId.toString())
                            .put(StrictCodes.USER_ID, chatMessageModel.getUserId())
                            .put(StrictCodes.CHAT_MESSAGE_BODY, chatMessageModel.getText())
                            .put(StrictCodes.CHAT_MESSAGE_TIME, chatMessageModel.getTime());

                    // send move
                    if(remotePlayMode.get() == REMOTE_LAN_PLAY){
                        output.writeUTF(payload.toString());
                        output.flush();
                        Log.i(Logs.INFO, "[Socket - send] " + GeneralUtilities.stringToPrettyJson(payload.toString()));
                        return;
                    }

                    gameWebSocket.send(payload.toString());
                    Log.i(Logs.INFO, "[WebSocket - send] " + GeneralUtilities.stringToPrettyJson(payload.toString()));

                }
                catch (JSONException | IOException e) {
                    Log.e(Logs.UNEXPECTED_ERROR,Logs.FUNCTION + "[sendChatMessage] can not" +
                            " create request body " + e);
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



    /**
     * helper for threads
     */
    private void startCountDownTimer(Thread thread) {

        // if connection is not made in some time abort connection
        new CountDownTimer(CONNECTION_TIMEOUT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // if game activity have been started only close count down timer
                // don`t close connection !!
                if (startGameActivity.get()) {
                    this.cancel();
                }

                if (abortGameConnection.get()) {
                    Log.i(Logs.INFO, "Countdown timer in thread [" + thread.getName() + "]  canceled.");
                    GeneralUtilities.showToast("Game aborted");

                    // stopping game will close socket connexion which will throw an exception in
                    // thread an thread will be stopped because socket will be closed
                    stopGame();

                    this.cancel();
                }
            }

            @Override
            public void onFinish() {
                // if game activity have been started don`t close connection !!
                if (startGameActivity.get()) {
                    return;
                }

                // game activity have not been started so stop game
                // stopping game will close socket connexion which will throw an exception in
                // thread an thread will be stopped because socket will be closed
                stopGame();

                Log.i(Logs.INFO, "Countdown timer in thread [" + thread.getName()
                        + "]  finished. Stopping thread [" + thread.getName() + "]");
            }
        }.start();

    }

    /** This server will start if is a LAN play */
    public void startSocketServer() {

        // close any previous connection
        closeSocketFlow(false);

        Thread thread = new Thread() {
            @Override
            public void run() {
                Log.i(Logs.INFO, "Thread [" + this.getName() + "] started");

                try {

                    // if game was aborted --> abort connection
                    if (abortGameConnection.get()) {
                        Log.i(Logs.INFO, "Thread [" + this.getName() + "] Game aborted");
                        return;
                    }

                    Log.i(Logs.INFO, "Thread [" + this.getName() + "] Starting local server");
                    localServerSocket = new ServerSocket(GeneralConfig.DEFAULT_DEVICE_PORT);

                    Log.i(Logs.INFO, "Server started on [IP: " + NetworkUtilities.getLocalIPAddress(true) +
                            " and PORT: " + localServerSocket.getLocalPort() + "]");
                    //Log.i(Logs.INFO,"Server started on [IP: " + InetAddress.getLocalHost() +
                    //" and PORT: " + localServerSocket.getLocalPort() + "]");

                    try {
                        // wait for connection
                        // this method is blocked until a connection is made
                        clientSocket = localServerSocket.accept();

                        // here a connection was made so open streams
                        output = new DataOutputStream(clientSocket.getOutputStream());
                        input = new DataInputStream(clientSocket.getInputStream());

                        Log.i(Logs.INFO, "Client connected on socket info [" + clientSocket.toString() + "]");

                        // mark game launch
                        startGameActivity.set(true);

                    } catch (IOException e) {
                        // mark this true and game will be stopped in timer for this thread
                        abortGameConnection.set(true);
                        Log.e(Logs.UNEXPECTED_ERROR, "[error 1] in thread [" + this.getName() + "]");
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    // mark this true and game will be stopped in timer for this thread
                    abortGameConnection.set(true);
                    Log.e(Logs.UNEXPECTED_ERROR, "[error 2] in thread [" + this.getName() + "]");
                    e.printStackTrace();
                } finally {
                    Log.i(Logs.INFO, "Thread [" + this.getName() + "] finished");
                }
            }
        };

        thread.setName("START_LOCAL_SERVER");
        thread.start();

        // if connection is not made in some time abort connection
        startCountDownTimer(thread);
    }

    /** Used in LAN play for connecting to opponent server */
    public void connectToSocketServer() {
        // close any previous connection
        closeSocketFlow(false);

        Thread thread = new Thread() {
            @Override
            public void run() {
                Log.i(Logs.INFO, "Thread [" + this.getName() + "] started");
                Log.i(Logs.INFO,"Connecting to server [" + opponentLocalIp + "] on port [" + GeneralConfig.DEFAULT_DEVICE_PORT +"]");

                AtomicBoolean connected = new AtomicBoolean(false);

                while(!connected.get() && !abortGameConnection.get()) {

                    try {

                        // socket = new Socket() is a blocking call
                        // when program advance then he is connected to server. If connection is not made
                        // in CONNECTION_TIMEOUT time then connection is aborted
                        clientSocket = new Socket(String.valueOf(opponentLocalIp), GeneralConfig.DEFAULT_DEVICE_PORT);

                        Log.i(Logs.INFO, "Connexion info: " + clientSocket.toString());

                        // open streams
                        input = new DataInputStream(clientSocket.getInputStream());
                        output = new DataOutputStream(clientSocket.getOutputStream());

                        // mark game launch
                        startGameActivity.set(true);

                        connected.set(true);

                        Log.i(Logs.INFO, "Thread [" + this.getName() + "] Connected to socket server ");

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i(Logs.INFO, "Thread [" + this.getName() + "] NOT connected to socket server ");

                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                Log.i(Logs.INFO, "Thread [" + this.getName() + "] finished");
            }
        };

        thread.setName("CONNECT_TO_SERVER");
        thread.start();

        // if connection is not made in some time abort connection
        startCountDownTimer(thread);
    }


    public void startCPUGame(BasicPlayMode basicPlayMode){

        // set current game activity
        this.basicPlayModeActivity = basicPlayMode;

        // TODO: change this and use string instead of character
        Character minPlayer = basicPlayMode.getSymbol(false).charAt(0);
        Character maxPlayer = basicPlayMode.getSymbol(true).charAt(0);
        Character firstPlayerToMove;
        if(canMakeMove.get()){
            firstPlayerToMove = minPlayer;
        }
        else{
            firstPlayerToMove = maxPlayer;
        }

        // TODO: add a difficulty box on layout
        int difficulty = 5;
        /*
        //start playing
        PlayGameService playGameService = new PlayGameService((byte) 1, minPlayer, maxPlayer,
                firstPlayerToMove, (byte)difficulty, (CpuPlay)basicPlayMode);

        Thread thread = new Thread(playGameService);
        thread.start();

         */
    }


    public void startRemoteGame(BasicPlayMode basicPlayMode){

        // set current game activity
        this.basicPlayModeActivity = basicPlayMode;

        // if is not player move choose the right waitForPlayerMove function based on play mode
        if(!canMakeMove.get()) {
            selectConnectionFunction(0, false, false);
        }

        // how much time is allocated for a turn (in seconds)
        AtomicInteger turnTime = new AtomicInteger(TURN_TIME);

        // game must be finished in TOTAL_GAME_TIME
        new CountDownTimer(TOTAL_GAME_TIME, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                //Log.i(Logs.INFO,"Time [MAIN_GAME_TIME] " + millisUntilFinished / 1000);

                int time = turnTime.decrementAndGet();
                ((RemotePlay)basicPlayModeActivity).updateTime(String.valueOf(time));

                // time for making the move is ended up --> game over
                if(time <= 0){

                    // show a specific message and disable game
                    String logMessage = "";
                    if(canMakeMove.get()){
                        logMessage = "You did not move in allocated time. You lose.";
                    }
                    else{
                        logMessage = "Opponent did not move in allocated time. You win.";
                    }

                    // disable game will call stopGame and everything will be closed so is no need
                    // to call another stopping function here
                    ((RemotePlay)basicPlayModeActivity).disableGame(false, logMessage);

                    this.cancel();
                    return;
                }

                if(abortGameConnection.get()){
                    ((RemotePlay)basicPlayModeActivity).disableGame(false,Logs.UNEXPECTED_ERROR + "Game aborted");
                    this.cancel();
                    return;
                }

                // if move was registered launch next move
                if(resetGameTimer.get()){
                    // reset turn time
                    turnTime.set(TURN_TIME);
                    resetGameTimer.set(false);
                    switch (remotePlayMode.get()){
                        case NO_REMOTE_MODE:
                            Log.e(Logs.UNEXPECTED_ERROR,"No remote mode selected in gameTimer");
                            // set this true and at the next onTick moment game will be stopped
                            abortGameConnection.set(true);
                            return;
                        case REMOTE_LAN_PLAY:
                            // if player can not move the he must wait for the opponent move
                            if(!canMakeMove.get()){
                                socketsWaitForTheOtherPlayerMove();
                            }
                            break;
                        case REMOTE_WEB_SOCKET_PLAY:
                            // if player can not move the he must wait for the opponent move
                            if(!canMakeMove.get()) {
                                webSocketWaitForTheOtherPlayerMove();
                            }
                            break;
                        default:
                            Log.e(Logs.UNEXPECTED_ERROR,"Unknown remote mode selected in gameTimer");
                            // set this true and at the next onTick moment game will be stopped
                            abortGameConnection.set(true);
                    }
                }

            }

            @Override
            public void onFinish() {
                // gameTimer will run for a maximum time, so in eventuality that an unexpected error
                // appears and this timer is not stopped when it should be using conditions from
                // onTick function then he will be stopped when count down is over

                if(basicPlayMode != null) {
                    ((RemotePlay)basicPlayModeActivity).updateTime("00");
                    ((RemotePlay)basicPlayModeActivity).disableGame(false, Logs.UNEXPECTED_ERROR +
                            "Game was active for to much time. Game disabled.");
                }
                Log.e(Logs.UNEXPECTED_ERROR,"Timer [MAIN_GAME_TIMER] finished because game count down was over.");
            }
        }.start();

    }

    public void stopGame(){
        // game was already stopped
        if(gameStopped.get()){
            Log.e(Logs.UNEXPECTED_ERROR,"Game was already stopped.");
            return;
        }

        Log.i(Logs.INFO,"Stopping game.");


        /* Make request here because when resetCurrentGameInfo() is called game code will be deleted.

        Send request to notify web socket server to abort game and delete existing connexion info.
        Is not very important if game will be stopped because connection will be automatically
        deleted by server after some time of inactivity. */

        // if message is sent means that these device will request to stop game
        if(abortGameConnection.get()){
            JSONObject payload = new JSONObject();
            try {
                // create payload
                payload.put(StrictCodes.TRANSMISSION_CODE, StrictCodes.STOP_GAME)
                        .put(StrictCodes.GAME_CODE, GameService.getGameServiceInstance().gameCode)
                        .put(StrictCodes.PLAYER_SYMBOL, GameService.getGameServiceInstance().playerSymbol);

                // send message
                gameWebSocket.send(payload.toString());
                Log.i(Logs.INFO, Logs.WEB_SOCKET_SENT_MESSAGE + GeneralUtilities.stringToPrettyJson(payload.toString()));
            } catch (JSONException e) {
                Log.e(Logs.UNEXPECTED_ERROR, Logs.FUNCTION + "[stopGame] can not" +" create payload body " + e);
                e.printStackTrace();
            }
            return;
        }


        // close socket connection (if is not an socket communication is no problem because checks
        // are  made in function)
        closeSocketFlow(true);

        // reset all info
        resetCurrentGameInfo();


        // mark game is stopped because he will be reset in resetCurrentGameInfo()
        // this will stop everything related to current game connection
        abortGameConnection.set(true);

        // mark game is stopped because he will be reset in resetCurrentGameInfo()
        // this will prevent to stop game a second time
        gameStopped.set(true);

        Log.i(Logs.INFO,"Game stopped");
    }

    public void resetCurrentGameInfo() {

        abortGameConnection.set(false);
        startGameActivity.set(false);
        gameStopped.set(false);
        stepOneCompleted.set(false);
        resetGameTimer.set(false);


        // current game configuration
        opponentLocalIp.delete(0,opponentLocalIp.length());
        gameCode.delete(0,gameCode.length());
        opponentJoined.set(false);
        initGameConnection.set(false);
        opponentMovePosition.set(-1);
        playerGenerateGame.set(false);
        canMakeMove.set(false);
        playerSymbol = StrictCodes.PLAYER_EMPTY_SYMBOL;

        basicPlayModeActivity = null;

        cpuDifficulty.set(1);
    }

    private void closeSocketFlow(boolean closeWebsocket) {
        try {
            // close web socket
            if(closeWebsocket && gameWebSocket != null){
                gameWebSocket.close(WebSocketService.getNormalClosureStatus(),"Game stopped");
            }

            // mark that socketThread must be stopped (this will have effect only if thread is running)
            stopSocketThread.set(true);

            // close streams
            if (output != null) {
                output.close();
            }
            if (input != null) {
                input.close();
            }

            // this will throw an exception and will force thread which use this socket to close
            if (clientSocket != null) {
                clientSocket.close();
            }

            // this will throw an exception and will force thread from startServer() to finish
            if (localServerSocket != null) {
                localServerSocket.close();
            }

            // interrupt socketThread if is on (when sockets are close will be automatically
            // interrupted by an exception)
            if (socketThread != null) {
                socketThread.interrupt();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(Logs.UNEXPECTED_ERROR, "Socket flow was not closed");
        }
    }



    private String createMovePayload(final int btnId){
        JSONObject payload = new JSONObject();
        try {
            // create payload
            payload.put(StrictCodes.TRANSMISSION_CODE, StrictCodes.GAME_MOVE)
                    .put(StrictCodes.GAME_CODE, gameCode)
                    .put(StrictCodes.WEBSOCKET_SESSION_ID, webSocketSessionId.toString())
                    .put(StrictCodes.PLAYER_SYMBOL, playerSymbol)
                    .put(StrictCodes.PLAYER_MOVE, btnId);

        }
        catch (JSONException e) {
            Log.e(Logs.UNEXPECTED_ERROR,Logs.FUNCTION + "[createMovePayload] can not" +
                    " create request body " + e);
            e.printStackTrace();
            GeneralUtilities.showToast(Logs.UNEXPECTED_ERROR + "Move cannot be send to server. Try again.");
            return null;
        }

        return payload.toString();
    }

    public void webSocketWaitForTheOtherPlayerMove(){
        canMakeMove.set(false);
        webSocketMoveReceived.set(false);

        // player should wait for move only a determined time
        // TURN_TIME is in seconds --> so make conversion to milliseconds
        new CountDownTimer(TURN_TIME * 1000,1000){

            @Override
            public void onTick(long millisUntilFinished) {

                Log.i(Logs.INFO,"Timer [webSocketWaitForMove] 00:" + millisUntilFinished / 1000);

                // if game was aborted stop timer
                if(abortGameConnection.get()){
                    Log.i(Logs.INFO,"Timer [webSocketWaitForMove] canceled because game connection was aborted");
                    this.cancel();
                    return;
                }

                // if move was received register cancel timer
                if(webSocketMoveReceived.get()){
                    webSocketMoveReceived.set(false);
                    Log.i(Logs.INFO,"Timer [webSocketWaitForMove] canceled because move was received");
                    this.cancel();
                }

            }

            @Override
            public void onFinish() {
                Log.i(Logs.INFO,"Timer [webSocketWaitForMove] finished");
            }
        }.start();
    }

    public void webSocketMakeYourMove(final int btnId){

        String payload = createMovePayload(btnId);

        // check if an error occurred
        if(payload == null){
            return;
        }

        // send move
        gameWebSocket.send(payload);
        Log.i(Logs.INFO, payload);

        canMakeMove.set(false);

        // notify game timer that you made your move
        resetGameTimer.set(true);

        // update gui
        ((RemotePlay)basicPlayModeActivity).updateTable(((RemotePlay)basicPlayModeActivity).getSymbol(false), btnId, Color.BLUE);

    }

    public void socketsMakeYourMove(final int btnId) {

        Thread thread = new Thread() {
            @Override
            public void run() {
                Log.i(Logs.INFO,"Thread [" + this.getName() + "] started");
                try {

                    String payload = createMovePayload(btnId);

                    // if game was already aborted or an error occurred while payload was constructed
                    if(abortGameConnection.get() || payload == null){
                        return;
                    }

                    // send move to the other player
                    output.writeUTF(payload);
                    output.flush();
                    Log.i(Logs.INFO,"[SOCKET - send]" + GeneralUtilities.stringToPrettyJson(payload));

                    // mark that player must wait for the opponent move
                    canMakeMove.set(false);

                    // notify game timer that you made your move
                    resetGameTimer.set(true);

                    // mark move on your screen
                    ((RemotePlay)basicPlayModeActivity).updateTable(((RemotePlay)basicPlayModeActivity).getSymbol(false), btnId,Color.BLUE);

                } catch (IOException e) {
                    e.printStackTrace();
                    GeneralUtilities.showToast(Logs.UNEXPECTED_ERROR + "Move cannot be send to server. Try again.");
                    //Log.e(Logs.UNEXPECTED_ERROR , " In thread [" + this.getName() + "] --> Move cannot be sent.");
                }
                finally {
                    Log.i(Logs.INFO,"Thread [" + this.getName() + "] finished");
                }
            }
        };
        thread.setName("THREAD_SOCKET_MAKE_YOUR_MOVE");
        thread.start();

    }

    public void socketsWaitForTheOtherPlayerMove() {

        // if socketThread is already running because was an unexpected error and was not close
        // first close the previous one
        stopSocketThread.set(true);
        if(socketThread != null){
            socketThread.interrupt();
        }

        // this thread will run until the move from opponent will be received or until the gameTimer
        // counter for turnTime expires (turnTimer became <= 0)
        socketThread = new Thread() {
            @Override
            public void run() {
                Log.i(Logs.INFO,"Thread [" + this.getName() + "] started");

                canMakeMove.set(false);
                stopSocketThread.set(false);

                while (!stopSocketThread.get()) {
                    try {
                        // get the other user move
                        Log.i(Logs.INFO,"Thread [" + this.getName() + "] waits for the opponent move");
                        final String message = input.readUTF();
                        Log.i(Logs.INFO,"[SOCKET - received]" +  GeneralUtilities.stringToPrettyJson(message));

                        if (message != null) {
                            onTextMessageReceived(null,null,message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(Logs.UNEXPECTED_ERROR,"In thread [" + this.getName() + "]. ");
                        stopSocketThread.set(true);
                        // when STOP GAME message is sent everything will be reset and this can be null
                        if(basicPlayModeActivity != null){
                            ((RemotePlay)basicPlayModeActivity).disableGame(false,Logs.UNEXPECTED_ERROR +
                                    " Wrong move format received. Game disabled.");
                        }
                    }
                }
                Log.i(Logs.INFO,"Thread [" + this.getName() + "] finished");
            }
        };

        socketThread.setName("THREAD_SOCKET_WAIT_FOR_MOVE");
        socketThread.start();

    }



    public boolean notYourMove(boolean showMessage) {
        if (canMakeMove.get()) {
            return false;
        }

        if (showMessage) {
            GeneralUtilities.showToast("Wait for next turn");
        }

        return true;
    }

    public boolean goodPlayerSymbol(){

        if(!playerSymbol.equals(StrictCodes.PLAYER_A_SYMBOL) && !playerSymbol.equals(StrictCodes.PLAYER_B_SYMBOL)){
            GeneralUtilities.showToast(Logs.UNEXPECTED_ERROR + " player symbol [" + playerSymbol + "] unrecognized");
            return false;
        }

        return true;
    }

    public void abortGameAlert(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("You you leave you will lose. You want to leave?");
        builder.setCancelable(false);

        builder.setPositiveButton(
                "Yes",
                (dialog, id) -> {
                    dialog.cancel();
                    ((RemotePlay)context).disableGame(true,"");
                });

        builder.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }

    public AtomicBoolean getAbortGameConnection() {
        return abortGameConnection;
    }

    public AtomicBoolean getGameStopped() {
        return gameStopped;
    }

    public void setGameWebSocket(WebSocket gameWebSocket) {
        this.gameWebSocket = gameWebSocket;
    }


    public DataOutputStream getOutput() {
        return output;
    }

}

