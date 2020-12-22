package com.licenta.smart_learn.utilities;


public interface Logs {

    // api messages

    // when request is good (is received by the api server and returns an error response)
    String API_BAD_REQUEST = "[api response]: ";
    String API_REQ_ERROR_TAG = "[Api-ERROR] ";
    String API_REQ_SUCCESS_TAG = "[Api-SUCCESS] ";
    String API_REQ_RESPONSE_TAG = "[Api-RESPONSE] ";
    String WEB_SOCKET_SENT_MESSAGE = "[WebSocket - SEND] ";
    String WEB_SOCKET_RECEIVED_MESSAGE = "[WebSocket - RECEIVED] ";

    // other messages
    String SUCCESS = "[SUCCESS] ";
    String INFO = "[INFO] ";
    String ERROR = "[ERROR] ";
    String UNEXPECTED_ERROR = "[UNEXPECTED ERROR] ";
    String FUNCTION = "[FUNCTION]";
}
