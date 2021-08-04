package com.smart_learn.data.firebase.firestore.entities;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;
import com.smart_learn.R;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.presenter.helpers.ApplicationController;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NotificationDocument {

    public interface Fields {
        String FROM_UID_FIELD_NAME = "fromUid";
        String FROM_DISPLAY_NAME_FIELD_NAME = "fromDisplayName";
        String FROM_DOCUMENT_REFERENCE_FIELD_NAME = "fromDocumentReference";
        String EXTRA_INFO_FIELD_NAME = "extraInfo";
        String MESSAGE_FIELD_NAME = "message";
        String TYPE_FIELD_NAME = "type";
        String MARKED_AS_READ_FIELD_NAME = "markedAsRead";
        String HIDDEN_FIELD_NAME = "hidden";
        String FINISHED_FIELD_NAME = "finished";
        String ACCEPTED_FIELD_NAME = "accepted";
        String DECLINED_FIELD_NAME = "declined";
        String RECEIVED_LESSON_FIELD_NAME = "receivedLesson";
        String RECEIVED_LESSON_WORD_LIST_FIELD_NAME = "receivedLessonWordList";
        String RECEIVED_LESSON_EXPRESSIONS_LIST_FIELD_NAME = "receivedLessonExpressionList";
    }

    public interface Types {
        int TYPE_NONE = 0;

        int TYPE_FRIEND_REQUEST_SENT = 1;
        int TYPE_FRIEND_REQUEST_RECEIVED = 2;
        int TYPE_FRIEND_REQUEST_ACCEPTED = 3;

        int TYPE_FRIEND_REMOVED_YOU = 4;
        int TYPE_YOU_REMOVED_FRIEND = 5;

        int TYPE_NORMAL_LESSON_RECEIVED = 6;
        int TYPE_NORMAL_LESSON_SENT = 7;
        int TYPE_SHARED_LESSON_RECEIVED = 8;
        int TYPE_SHARED_LESSON_SENT = 9;

        int TYPE_WORD_RECEIVED = 10;
        int TYPE_EXPRESSION_RECEIVED = 11;

        int TYPE_ONLINE_TEST_INVITATION_RECEIVED = 12;
    }

    private DocumentMetadata documentMetadata;

    // these will be the UID of the user who sent the notifications
    private String fromUid;

    // These will be the display name of the user who sent the notifications. I use display name
    // and UID in order do avoid a read for the user document with specific UID.
    private String fromDisplayName;

    // for fromUserReference
    private DocumentReference fromDocumentReference;

    private int type;
    private boolean markedAsRead;
    private boolean hidden;
    // mark that notification has been processed
    private boolean finished;

    /* *********************************************************************************************
                                    START  Optional Fields

                        These fields are used only for some notifications types
    ********************************************************************************************* */

    // Used to save Lesson name, Word, Expression or Friend Display name if is a notification of that
    // type. Otherwise it will be empty.
    private String extraInfo;

    // Used to store an optional message from the user who sends the notification, if necessary.
    private String message;

    // used for notifications with friend request type
    private boolean accepted;
    private boolean declined;

    private String receivedLesson;
    private String receivedLessonWordList;
    private String receivedLessonExpressionList;

    /* *********************************************************************************************
                                    END  Optional Fields
    ********************************************************************************************* */


    // Not needed in firestore document. Used to set description of the notification based on type,
    // in order to be readable for the user and to give more info`s than title.
    @Exclude
    private String description;

    // Not needed in firestore document. Used to set title of the notification based on type, in
    // order to be readable for the user.
    @Exclude
    private String title;

    public NotificationDocument() {
        // needed for Firestore
    }

    public NotificationDocument(DocumentMetadata documentMetadata, String fromUid, String fromDisplayName,
                                DocumentReference fromDocumentReference, int type) {
        this.documentMetadata = documentMetadata;
        this.fromUid = fromUid;
        this.fromDisplayName = fromDisplayName;
        this.fromDocumentReference = fromDocumentReference;
        this.type = type;
    }

    /**
     * This getter must have this form in order to work with Firestore. If you leave getter generated
     * by Lombok using @Getter then isMarkedAsRead() will be generated and will not work with Firestore.
     * */
    public boolean getMarkedAsRead() {
        return markedAsRead;
    }


    /**
     * This getter must have this form in order to work with Firestore. If you leave getter generated
     * by Lombok using @Getter then isHidden() will be generated and will not work with Firestore.
     * */
    public boolean getHidden() {
        return hidden;
    }

    /**
     * This getter must have this form in order to work with Firestore. If you leave getter generated
     * by Lombok using @Getter then isFinished() will be generated and will not work with Firestore.
     * */
    public boolean getFinished() {
        return finished;
    }

    /**
     * This getter must have this form in order to work with Firestore. If you leave getter generated
     * by Lombok using @Getter then isAccepted() will be generated and will not work with Firestore.
     * */
    public boolean getAccepted() {
        return accepted;
    }

    /**
     * This getter must have this form in order to work with Firestore. If you leave getter generated
     * by Lombok using @Getter then isDeclined() will be generated and will not work with Firestore.
     * */
    public boolean getDeclined() {
        return declined;
    }

    /**
     * Field description is NOT needed in the firestore document.
     *
     * https://stackoverflow.com/questions/49865558/firestore-where-exactly-to-put-the-exclude-annotation
     * */
    @Exclude
    public String getDescription() {
        return description;
    }

    /**
     * Field title is NOT needed in the firestore document.
     *
     * https://stackoverflow.com/questions/49865558/firestore-where-exactly-to-put-the-exclude-annotation
     * */
    @Exclude
    public String getTitle() {
        return title;
    }

    /**
     * Use to generate a custom title based on the notification type.
     *
     * @param type The notification type.
     *
     * @return A resource string id of the custom title.
     * */
    public static int generateNotificationTitle(int type){
        switch (type){
            case Types.TYPE_FRIEND_REQUEST_SENT:
                return R.string.friend_request_sent_title;
            case Types.TYPE_FRIEND_REQUEST_RECEIVED:
                return R.string.friend_request_received_title;
            case Types.TYPE_FRIEND_REQUEST_ACCEPTED:
                return R.string.friend_request_accepted_title;

            case Types.TYPE_FRIEND_REMOVED_YOU:
                return R.string.friend_removed_you_title;
            case Types.TYPE_YOU_REMOVED_FRIEND:
                return R.string.you_removed_friend_title;

            case Types.TYPE_NORMAL_LESSON_RECEIVED:
                return R.string.normal_lesson_received_title;
            case Types.TYPE_NORMAL_LESSON_SENT:
                return R.string.normal_lesson_sent_title;
            case Types.TYPE_SHARED_LESSON_RECEIVED:
                return R.string.shared_lesson_received_title;
            case Types.TYPE_SHARED_LESSON_SENT:
                return R.string.shared_lesson_sent_title;

            case Types.TYPE_WORD_RECEIVED:
                return R.string.word_received_title;
            case Types.TYPE_EXPRESSION_RECEIVED:
                return R.string.expression_received_title;

            case Types.TYPE_ONLINE_TEST_INVITATION_RECEIVED:
                return R.string.online_test_invitation_received_title;

            case Types.TYPE_NONE:
            default:
                return R.string.new_notification_title;
        }
    }

    /**
     * Use to generate a custom description based on the notification type.
     *
     * @param type The notification type.
     * @param extraInfo The notification extra info.
     *
     * @return A string which represents the custom description.
     * */
    public static String generateNotificationDescription(int type, @Nullable String extraInfo){
        String tmp = "";
        if(extraInfo != null){
            tmp = extraInfo;
        }

        ApplicationController applicationController = ApplicationController.getInstance();
        switch (type){
            case Types.TYPE_FRIEND_REQUEST_SENT:
                return applicationController.getString(R.string.friend_request_sent_description_1) + " " + tmp + ". " +
                    applicationController.getString(R.string.friend_request_sent_description_2);
            case Types.TYPE_FRIEND_REQUEST_RECEIVED:
                return applicationController.getString(R.string.friend_request_received_description) + " " + tmp + ".";
            case Types.TYPE_FRIEND_REQUEST_ACCEPTED:
                return applicationController.getString(R.string.friend_request_accepted_description_1) + " " + tmp + ". " +
                        applicationController.getString(R.string.friend_request_accepted_description_2);

            case Types.TYPE_FRIEND_REMOVED_YOU:
                return applicationController.getString(R.string.friend_removed_you_description_1) + " " + tmp + ". " +
                        applicationController.getString(R.string.friend_removed_you_description_2);
            case Types.TYPE_YOU_REMOVED_FRIEND:
                return applicationController.getString(R.string.you_removed_friend_description_1) + " " + tmp + ". " +
                        applicationController.getString(R.string.you_removed_friend_description_2);

            case Types.TYPE_NORMAL_LESSON_RECEIVED:
                return applicationController.getString(R.string.normal_lesson_received_description_1) + " " + tmp + ". " +
                        applicationController.getString(R.string.normal_lesson_received_description_2);
            case Types.TYPE_NORMAL_LESSON_SENT:
                return applicationController.getString(R.string.normal_lesson_sent_description_1) + " " + tmp + ". " +
                        applicationController.getString(R.string.normal_lesson_sent_description_2);
            case Types.TYPE_SHARED_LESSON_RECEIVED:
                return applicationController.getString(R.string.shared_lesson_received_description_1) + " " + tmp + ". " +
                        applicationController.getString(R.string.shared_lesson_received_description_2);
            case Types.TYPE_SHARED_LESSON_SENT:
                return applicationController.getString(R.string.shared_lesson_sent_description_1) + " " + tmp + ". " +
                        applicationController.getString(R.string.shared_lesson_sent_description_2);

            case Types.TYPE_WORD_RECEIVED:
                return applicationController.getString(R.string.word_received_description_1) + " " + tmp + ". " +
                        applicationController.getString(R.string.word_received_description_2);
            case Types.TYPE_EXPRESSION_RECEIVED:
                return applicationController.getString(R.string.expression_received_description_1) + " " + tmp + ". " +
                        applicationController.getString(R.string.expression_received_description_2);

            case Types.TYPE_ONLINE_TEST_INVITATION_RECEIVED:
                return applicationController.getString(R.string.online_test_invitation_received_description_1) + " " + tmp + ". " +
                        applicationController.getString(R.string.online_test_invitation_received_description_2);

            case Types.TYPE_NONE:
            default:
                return applicationController.getString(R.string.empty);
        }
    }

    public static HashMap<String, Object> convertDocumentToHashMap(NotificationDocument notificationDocument){
        if(notificationDocument == null){
            return new HashMap<>();
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put(DocumentMetadata.Fields.DOCUMENT_METADATA_FIELD_NAME, DocumentMetadata.convertDocumentToHashMap(notificationDocument.getDocumentMetadata()));
        data.put(Fields.FROM_UID_FIELD_NAME, notificationDocument.getFromUid());
        data.put(Fields.FROM_DISPLAY_NAME_FIELD_NAME, notificationDocument.getFromDisplayName());
        data.put(Fields.FROM_DOCUMENT_REFERENCE_FIELD_NAME, notificationDocument.getFromDocumentReference());
        data.put(Fields.EXTRA_INFO_FIELD_NAME, notificationDocument.getExtraInfo());
        data.put(Fields.MESSAGE_FIELD_NAME, notificationDocument.getMessage());
        data.put(Fields.TYPE_FIELD_NAME, notificationDocument.getType());
        data.put(Fields.MARKED_AS_READ_FIELD_NAME, notificationDocument.getMarkedAsRead());
        data.put(Fields.HIDDEN_FIELD_NAME, notificationDocument.getHidden());
        data.put(Fields.FINISHED_FIELD_NAME, notificationDocument.getFinished());
        data.put(Fields.ACCEPTED_FIELD_NAME, notificationDocument.getAccepted());
        data.put(Fields.DECLINED_FIELD_NAME, notificationDocument.getDeclined());
        data.put(Fields.RECEIVED_LESSON_FIELD_NAME, notificationDocument.getReceivedLesson());
        data.put(Fields.RECEIVED_LESSON_WORD_LIST_FIELD_NAME, notificationDocument.getReceivedLessonWordList());
        data.put(Fields.RECEIVED_LESSON_EXPRESSIONS_LIST_FIELD_NAME, notificationDocument.getReceivedLessonExpressionList());

        return data;
    }

}
