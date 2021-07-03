package com.smart_learn.data.firebase.firestore.entities;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;
import com.smart_learn.R;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.helpers.DataUtilities;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import timber.log.Timber;

@Getter
@Setter
public class NotificationDocument {

    public static final String FROM_UID_FIELD_NAME = "fromUid";
    public static final String FROM_DISPLAY_NAME_FIELD_NAME = "fromDisplayName";
    public static final String TYPE_FIELD_NAME = "type";
    public static final String DESCRIPTION_FIELD_NAME = "description";
    public static final String MARKED_AS_READ_FIELD_NAME = "markedAsRead";
    public static final String HIDDEN_FIELD_NAME = "hidden";

    // if notification is not set
    public static final int TYPE_NONE = 0;
    // used when user receives a new friend request
    public static final int TYPE_NEW_FRIEND_REQUEST = 1;
    // used when user received a lesson from a friend
    public static final int TYPE_NEW_RECEIVED_LESSON = 2;
    // used when user received a word from a friend
    public static final int TYPE_NEW_RECEIVED_WORD = 3;
    // used when user received a expression from a friend
    public static final int TYPE_NEW_RECEIVED_EXPRESSION = 4;
    // used when user is invited in a shared lesson by a friend
    public static final int TYPE_NEW_SHARED_LESSON = 5;


    private DocumentMetadata documentMetadata;

    // these will be the UID of the user who sent the notifications
    private String fromUid;
    // These will be the display name of the user who sent the notifications. I use display name
    // and UID in order do avoid a read for the user document with specific UID.
    private String fromDisplayName;
    private int type;
    private String description;
    private boolean markedAsRead;
    private boolean hidden;

    // Not needed in firestore document. Used to set title of the notification based on type, in
    // order to be readable for the user.
    @Exclude
    private String title;

    public NotificationDocument() {
        // needed for Firestore
    }

    public NotificationDocument(@NonNull @NotNull DocumentMetadata documentMetadata,
                                @NonNull @NotNull String fromUid, @NonNull @NotNull String fromDisplayName,
                                int type, String description, boolean markedAsRead, boolean hidden) {
        this.documentMetadata = documentMetadata;
        this.fromUid = fromUid;
        this.fromDisplayName = fromDisplayName;
        this.type = type;
        this.description = description;
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

    public static boolean isGoodNotificationDocumentConfiguration(DocumentSnapshot documentSnapshot){
        if(!DataUtilities.Firestore.isGoodDocumentSnapshot(documentSnapshot)){
            Timber.w("documentSnapshot is not valid");
            return false;
        }

        // check if the provided documentSnapshot contains all fields from the notification document
        if(!documentSnapshot.contains(FROM_UID_FIELD_NAME)){
            Timber.w("documentSnapshot does not contain field %s", FROM_UID_FIELD_NAME);
            return false;
        }

        if(!documentSnapshot.contains(FROM_DISPLAY_NAME_FIELD_NAME)){
            Timber.w("documentSnapshot does not contain field %s", FROM_DISPLAY_NAME_FIELD_NAME);
            return false;
        }

        if(!documentSnapshot.contains(TYPE_FIELD_NAME)){
            Timber.w("documentSnapshot does not contain field %s", TYPE_FIELD_NAME);
            return false;
        }

        if(!documentSnapshot.contains(DESCRIPTION_FIELD_NAME)){
            Timber.w("documentSnapshot does not contain field %s", DESCRIPTION_FIELD_NAME);
            return false;
        }

        if(!documentSnapshot.contains(MARKED_AS_READ_FIELD_NAME)){
            Timber.w("documentSnapshot does not contain field %s", MARKED_AS_READ_FIELD_NAME);
            return false;
        }

        if(!documentSnapshot.contains(HIDDEN_FIELD_NAME)){
            Timber.w("documentSnapshot does not contain field %s", HIDDEN_FIELD_NAME);
            return false;
        }

        if(!DocumentMetadata.isGoodDocumentMetadataConfiguration(documentSnapshot)){
            Timber.w("documentSnapshot metadata is not correct");
            return false;
        }

        return true;
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
            case TYPE_NEW_FRIEND_REQUEST:
                return R.string.new_friend_request;
            case TYPE_NEW_RECEIVED_LESSON:
                return R.string.new_received_lesson;
            case TYPE_NEW_RECEIVED_WORD:
                return R.string.new_received_word;
            case TYPE_NEW_RECEIVED_EXPRESSION:
                return R.string.new_received_expression;
            case TYPE_NEW_SHARED_LESSON:
                return R.string.new_shared_lesson;
            case TYPE_NONE:
            default:
                return R.string.new_notification;
        }
    }

}
