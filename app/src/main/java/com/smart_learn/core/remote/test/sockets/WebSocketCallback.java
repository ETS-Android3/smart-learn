package com.smart_learn.core.remote.test.sockets;

import android.util.Log;

import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.core.utilities.Logs;

import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;

public interface WebSocketCallback {
    void onOpen(WebSocket webSocket, Response response);
    void onTextMessage(WebSocket webSocket, String text);
    default void onBinaryMessage(WebSocket webSocket, ByteString bytes){
        GeneralUtilities.notImplemented();
    }

    default void onClosing(WebSocket webSocket, int code, String reason){
        Log.i(Logs.INFO,"Closing web socket [" + webSocket.toString() + "] with code [" + code
                + "]  for reason [" + reason + "]");
        webSocket.close(WebSocketService.getNormalClosureStatus(), null);
    }

    default void onFailure(WebSocket webSocket, Throwable t, Response response){
        Log.e(Logs.UNEXPECTED_ERROR,"On failure web socket [" + webSocket.toString() + "]  " +
                "with throwable message [" + t.getMessage() + "] and response " + " [" + response + "]");
    }
}
