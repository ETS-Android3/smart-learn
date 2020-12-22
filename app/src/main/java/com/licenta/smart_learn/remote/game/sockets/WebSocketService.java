package com.licenta.smart_learn.remote.game.sockets;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

/** Singleton class */
public final class WebSocketService {

    public static final int WEB_SOCKET_REQUEST_ERROR = 100;
    private static WebSocketService webSocketServiceInstance = null;
    private OkHttpClient okHttpClient;

    private static final int NORMAL_CLOSURE_STATUS = 1000;

    private WebSocketService() {
        okHttpClient = new OkHttpClient();
    }

    public static WebSocketService getWebSocketServiceInstance(){
        if(webSocketServiceInstance == null){
            webSocketServiceInstance = new WebSocketService();
        }
        return webSocketServiceInstance;
    }

    public WebSocket connectToWebSocket(String url, WebSocketCallback webSocketCallback){
        Request request = new Request.Builder().url(url).build();
        EchoWebSocketListener listener = new EchoWebSocketListener(webSocketCallback);
        return okHttpClient.newWebSocket(request, listener);
    }

    private void closeOkHttpClient(){
        okHttpClient.dispatcher().executorService().shutdown();
    }

    public static int getNormalClosureStatus() {
        return NORMAL_CLOSURE_STATUS;
    }
}
