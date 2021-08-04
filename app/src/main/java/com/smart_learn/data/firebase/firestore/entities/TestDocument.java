package com.smart_learn.data.firebase.firestore.entities;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;
import com.smart_learn.data.entities.Test;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TestDocument extends Test {

    // At boolean fields if 'is' appears in front leave fields without 'is'  in
    // order to work with Firestore.
    public interface Fields {
        String IS_COUNTED_AS_FINISHED_FIELD_NAME = "countedAsFinished";
        String IS_ONLINE_FIELD_NAME = "online";
        String CONTAINER_TEST_ID_FIELD_NAME = "containerTestId";
        String USER_DISPLAY_NAME_FIELD_NAME = "userDisplayName";
        String USER_EMAIL_FIELD_NAME = "userEmail";
        String USER_PROFILE_PHOTO_URL_FIELD_NAME = "userProfilePhotoUrl";
        String PARTICIPANTS_FIELD_NAME = "participants";
    }

    @Getter
    private DocumentMetadata documentMetadata;

    // used for local/online unscheduled tests in order to make a correct update on user counters
    private boolean isCountedAsFinished;
    // mark that test is online or local
    private boolean isOnline;
    // used to store id of the container test for a fast access if needed
    private String containerTestId = "";
    // used to store UID of the participants
    private ArrayList<String> participants = new ArrayList<>();

    // used for a faster access at user data
    private String userDisplayName = "";
    private String userEmail = "";
    private String userProfilePhotoUrl = "";

    @Exclude
    @Nullable
    private ArrayList<DocumentSnapshot> selectedFriends;

    public TestDocument() {
        // needed for Firestore
    }

    public TestDocument(DocumentMetadata documentMetadata) {
        super();
        this.documentMetadata = documentMetadata;
    }

    public void setContainerTestId(String containerTestId) {
        this.containerTestId = containerTestId == null ? "" : containerTestId;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName == null ? "" : userDisplayName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail == null ? "" : userEmail;
    }

    public void setUserProfilePhotoUrl(String userProfilePhotoUrl) {
        this.userProfilePhotoUrl = userProfilePhotoUrl == null ? "" : userProfilePhotoUrl;
    }

    public void setParticipants(ArrayList<String> participants) {
        this.participants = participants == null ? new ArrayList<>() : participants;
    }

    public static HashMap<String, Object> convertDocumentToHashMap(TestDocument testDocument){
        if(testDocument == null){
            return new HashMap<>();
        }

        HashMap<String, Object> data = Test.convertDocumentToHashMap(testDocument);
        data.put(DocumentMetadata.Fields.DOCUMENT_METADATA_FIELD_NAME, DocumentMetadata.convertDocumentToHashMap(testDocument.getDocumentMetadata()));
        data.put(Fields.IS_COUNTED_AS_FINISHED_FIELD_NAME, testDocument.isCountedAsFinished());
        data.put(Fields.IS_ONLINE_FIELD_NAME, testDocument.isOnline());
        data.put(Fields.CONTAINER_TEST_ID_FIELD_NAME, testDocument.getContainerTestId());
        data.put(Fields.USER_DISPLAY_NAME_FIELD_NAME, testDocument.getUserDisplayName());
        data.put(Fields.USER_EMAIL_FIELD_NAME, testDocument.getUserEmail());
        data.put(Fields.USER_PROFILE_PHOTO_URL_FIELD_NAME, testDocument.getUserProfilePhotoUrl());
        data.put(Fields.PARTICIPANTS_FIELD_NAME, testDocument.getParticipants());

        return data;
    }

    /**
     * Field selected is NOT needed in the firestore document.
     *
     * https://stackoverflow.com/questions/49865558/firestore-where-exactly-to-put-the-exclude-annotation
     * */
    @Exclude
    public @Nullable ArrayList<DocumentSnapshot> getSelectedFriends() {
        return selectedFriends;
    }

}
