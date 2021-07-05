package com.smart_learn.data.firebase.firestore.entities;

import com.google.firebase.firestore.Exclude;
import com.smart_learn.R;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.presenter.helpers.ApplicationController;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class NotificationDocument {

    public interface Fields {
        String FROM_UID_FIELD_NAME = "fromUid";
        String FROM_DISPLAY_NAME_FIELD_NAME = "fromDisplayName";
        String EXTRA_INFO_FIELD_NAME = "extraInfo";
        String MESSAGE_FIELD_NAME = "message";
        String TYPE_FIELD_NAME = "type";
        String MARKED_AS_READ_FIELD_NAME = "markedAsRead";
        String HIDDEN_FIELD_NAME = "hidden";
    }

    public interface Types {
        int TYPE_NONE = 0;                    // if notification is not set
        int TYPE_NEW_FRIEND_REQUEST = 1;      // used when user receives a new friend request
        int TYPE_NEW_RECEIVED_LESSON = 2;     // used when user received a lesson from a friend
        int TYPE_NEW_RECEIVED_WORD = 3;       // used when user received a word from a friend
        int TYPE_NEW_RECEIVED_EXPRESSION = 4; // used when user received a expression from a friend
        int TYPE_NEW_SHARED_LESSON = 5;       // used when user is invited in a shared lesson by a friend
    }

    private DocumentMetadata documentMetadata;

    // these will be the UID of the user who sent the notifications
    private String fromUid;

    // These will be the display name of the user who sent the notifications. I use display name
    // and UID in order do avoid a read for the user document with specific UID.
    private String fromDisplayName;

    // Used to save Lesson name, Word or Expression if is a notification of that type. Otherwise
    // it will be empty.
    private String extraInfo;

    // Used to store an optional message from the user who sends the notification, if necessary.
    private String message;

    private int type;
    private boolean markedAsRead;
    private boolean hidden;

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

    public NotificationDocument(@NonNull @NotNull DocumentMetadata documentMetadata, String fromUid, String fromDisplayName,
                                String extraInfo, String message, int type, boolean markedAsRead, boolean hidden) {
        this.documentMetadata = documentMetadata;
        this.fromUid = fromUid == null ? "" : fromUid;
        this.fromDisplayName = fromDisplayName == null ? "" : fromDisplayName;
        this.extraInfo = extraInfo == null ? "" : extraInfo;
        this.message = message == null ? "" : message;
        this.type = type;
        this.markedAsRead = markedAsRead;
        this.hidden = hidden;
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
            case Types.TYPE_NEW_FRIEND_REQUEST:
                return R.string.new_friend_request_title;
            case Types.TYPE_NEW_RECEIVED_LESSON:
                return R.string.new_received_lesson_title;
            case Types.TYPE_NEW_RECEIVED_WORD:
                return R.string.new_received_word_title;
            case Types.TYPE_NEW_RECEIVED_EXPRESSION:
                return R.string.new_received_expression_title;
            case Types.TYPE_NEW_SHARED_LESSON:
                return R.string.new_shared_lesson_title;
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
            case Types.TYPE_NEW_FRIEND_REQUEST:
                return applicationController.getString(R.string.new_friend_request_description);
            case Types.TYPE_NEW_RECEIVED_LESSON:
                return applicationController.getString(R.string.new_received_lesson_description_1) + " " + tmp + ". " +
                        applicationController.getString(R.string.new_received_lesson_description_2);
            case Types.TYPE_NEW_RECEIVED_WORD:
                return applicationController.getString(R.string.new_received_word_description_1) + " " + tmp + ". " +
                        applicationController.getString(R.string.new_received_word_description_2);
            case Types.TYPE_NEW_RECEIVED_EXPRESSION:
                return applicationController.getString(R.string.new_received_expression_description_1) + " " + tmp + ". " +
                        applicationController.getString(R.string.new_received_expression_description_2);
            case Types.TYPE_NEW_SHARED_LESSON:
                return applicationController.getString(R.string.new_shared_lesson_description_1) + " " + tmp + ". " +
                        applicationController.getString(R.string.new_shared_lesson_description_2);
            case Types.TYPE_NONE:
            default:
                return applicationController.getString(R.string.empty);
        }
    }

}
