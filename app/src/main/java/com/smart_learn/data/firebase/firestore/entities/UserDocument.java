package com.smart_learn.data.firebase.firestore.entities;

import com.smart_learn.data.firebase.firestore.entities.helpers.BasicProfileDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDocument extends BasicProfileDocument {

    public interface Fields {
        String RECEIVED_REQUESTS_FIELD_NAME = "receivedRequest";
        String PENDING_FRIENDS_FIELD_NAME = "pendingFriends";
        String FRIENDS_FIELD_NAME = "friends";
        String NR_OF_UNREAD_NOTIFICATIONS_FIELD_NAME = "nrOfUnreadNotifications";
    }

    private ArrayList<String> receivedRequest;
    private ArrayList<String> pendingFriends;
    private ArrayList<String> friends;

    // counters
    private long nrOfUnreadNotifications;

    public UserDocument() {
        // needed for Firestore
    }

    public UserDocument(DocumentMetadata documentMetadata, String email, String displayName, String profilePhotoUrl,
                        ArrayList<String> receivedRequest, ArrayList<String> pendingFriends, ArrayList<String> friends) {
        super(documentMetadata, email, displayName, profilePhotoUrl);
        this.receivedRequest = receivedRequest;
        this.pendingFriends = pendingFriends;
        this.friends = friends;
    }
}