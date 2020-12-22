package com.licenta.smart_learn.remote.game.config;

/** This codes are used in socket server and api server , so must be the same */
public interface StrictCodes {
    String DELIMITER = "#";
    String PLAYER_A_SYMBOL = "A";
    String PLAYER_B_SYMBOL = "B";
    String PLAYER_EMPTY_SYMBOL = "NO-SYMBOL";

    // api codes
    String API_TAG_SUCCESS = "success";
    String API_TAG_ERROR = "error";


    // socket connection codes
    String GAME_ERROR = "GAME_ERROR";
    String NEW_CONNEXION = "NEW_CONNECTION";
    String JOIN_CONNEXION = "JOIN_CONNECTION";
    String WRONG_CODE = "WRONG_CODE";
    String GAME_CODE_RECEIVED = "GAME_CODE_RECEIVED";
    String START_GAME = "START_GAME";
    String GAME_MOVE = "GAME_MOVE";
    String GAME_CHAT_MESSAGE = "GAME_CHAT_MESSAGE";
    String STOP_GAME = "STOP_GAME";



    //JSON field codes
    String TRANSMISSION_CODE = "transmission_code";
    String INFO_MESSAGE = "info_message";
    String PUBLIC_IP = "public_ip";
    String LOCAL_IP = "local_ip";
    String LOCALHOST = "localhost";
    String PLAYER_SYMBOL = "player_symbol";
    String FIRST_TO_MOVE = "first_to_move";
    String GAME_CODE = "game_code";
    String PLAYER_MOVE = "player_move";
    String PLAYERS_NUMBER = "players_number";
    String OPPONENT_SYMBOL = "opponent_symbol";
    String WEBSOCKET_SESSION_ID = "websocket_session_id";
    String CHAT_MESSAGE_BODY = "chat_message_body";
    String CHAT_MESSAGE_TIME = "chat_message_time";
    String USER_ID = "user_id";

}

