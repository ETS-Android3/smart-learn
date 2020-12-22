package com.licenta.smart_learn.config;


public interface GeneralConfig {

    // connection port for this device if this device will become a server
    int DEFAULT_DEVICE_PORT = 15000;

    // connection info about api server
    String SERVER_ADDRESS = "http://192.168.1.54:8080";

    // web socket endpoints
    String ULR_WEB_SOCKET_CONNECT_TO_GAME = "ws://192.168.1.54:8080/game";
}
