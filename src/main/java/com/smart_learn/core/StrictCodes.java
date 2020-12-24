package com.smart_learn.core;

/** this message must be the same on devices also */
public interface StrictCodes {
    String DELIMITER = "#";

    // api codes
    String API_TAG_SUCCESS = "success";
    String API_TAG_ERROR = "error";

    // socket connection codes
    String TEST_ERROR = "TEST_ERROR";
    String NEW_CONNEXION = "NEW_CONNECTION";
    String JOIN_CONNEXION = "JOIN_CONNECTION";
    String WRONG_CODE = "WRONG_CODE";
    String PREPARE_TEST = "PREPARE_TEST";
    String START_TEST = "START_TEST";
    String TEST_RESPONSE = "TEST_RESPONSE";
    String TEST_CHAT_MESSAGE = "TEST_CHAT_MESSAGE";
    String STOP_TEST = "STOP_TEST";
    String PARTICIPANT_CONNECTED = "PARTICIPANT_CONNECTED";
    String PARTICIPANT_DISCONNECTED = "PARTICIPANT_DISCONNECTED";
    String PARTICIPANTS = "PARTICIPANTS";



    //JSON field codes
    String TRANSMISSION_CODE = "transmission_code";
    String INFO_MESSAGE = "info_message";
    String TEST_CODE = "test_code";
    String TEST_RESPONSE_BODY = "test_response_body";
    String TEST_QUESTIONS = "test_questions";
    String PARTICIPANTS_NUMBER = "participants_number";
    String QUESTION_ID = "question_id";
    String QUESTION = "question";
    String RESPONSE = "response";
    String CORRECT_RESPONSE = "correct_response";
    String WEBSOCKET_SESSION_ID = "websocket_session_id";
    String CHAT_MESSAGE_BODY = "chat_message_body";
    String CHAT_MESSAGE_TIME = "chat_message_time";
    String USER_ID = "user_id";
    String PARTICIPANT_ID = "participant_id";
    String PARTICIPANT_IS_ADMIN = "participant_is_admin";

}
