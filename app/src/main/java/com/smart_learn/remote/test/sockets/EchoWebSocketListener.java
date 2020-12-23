package com.smart_learn.remote.test.sockets;

import android.util.Log;

import com.smart_learn.utilities.GeneralUtilities;
import com.smart_learn.utilities.Logs;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public final class EchoWebSocketListener extends WebSocketListener {


    private WebSocketCallback webSocketCallback;

    public EchoWebSocketListener(WebSocketCallback webSocketCallback) {
        this.webSocketCallback = webSocketCallback;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.i(Logs.INFO,"WebSocket opened [" + webSocket.toString() + "]");
        webSocketCallback.onOpen(webSocket,response);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.i(Logs.INFO,Logs.WEB_SOCKET_RECEIVED_MESSAGE + "[" + webSocket.toString() +
                "] Receiving message : " + GeneralUtilities.stringToPrettyJson(text));
        webSocketCallback.onTextMessage(webSocket,text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        Log.i(Logs.INFO,Logs.WEB_SOCKET_RECEIVED_MESSAGE + "[" + webSocket.toString() +
                "] Receiving bytes : " + bytes.hex());
        webSocketCallback.onBinaryMessage(webSocket,bytes);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocketCallback.onClosing(webSocket,code,reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        webSocketCallback.onFailure(webSocket,t,response);
    }

}
