package com.smart_learn.data.user.firebase.firestore.entities;

import android.text.SpannableString;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;
import com.smart_learn.data.user.firebase.firestore.entities.helpers.BasicProfileDocument;
import com.smart_learn.data.user.firebase.firestore.entities.helpers.DocumentMetadata;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FriendDocument extends BasicProfileDocument {

    public interface Fields {
        String FRIEND_UID_FIELD_NAME = "friendUid";
        String FRIENDS_SINCE_FIELD_NAME = "friendsSince";
        String USER_DOCUMENT_REFERENCE_FIELD_NAME = "userDocumentReference";
    }

    // used to store friend UID
    private String friendUid;
    // used to store a timestamp for time when friend request is accepted
    private long friendsSince;
    // used to sync values from friend document
    private DocumentReference userDocumentReference;

    @Exclude
    private SpannableString spannedEmail;
    @Exclude
    private SpannableString spannedDisplayName;


    public FriendDocument() {
        // needed for Firestore
    }

    public FriendDocument(DocumentMetadata documentMetadata, String email, String displayName, String profilePhotoUrl,
                          String friendUid, long friendsSince, DocumentReference userDocumentReference) {
        super(documentMetadata, email, displayName, profilePhotoUrl);
        this.friendUid = friendUid;
        this.friendsSince = friendsSince;
        this.userDocumentReference = userDocumentReference;
    }


    @Exclude
    public SpannableString getSpannedEmail() {
        return spannedEmail;
    }

    @Exclude
    public SpannableString getSpannedDisplayName() {
        return spannedDisplayName;
    }

    public static HashMap<String, Object> convertDocumentToHashMap(FriendDocument document){
        if(document == null){
            return new HashMap<>();
        }

        HashMap<String, Object> data = BasicProfileDocument.convertDocumentToHashMap(document);
        data.put(Fields.FRIEND_UID_FIELD_NAME, document.getFriendUid());
        data.put(FriendDocument.Fields.FRIENDS_SINCE_FIELD_NAME, document.getFriendsSince());
        data.put(FriendDocument.Fields.USER_DOCUMENT_REFERENCE_FIELD_NAME, document.getUserDocumentReference());

        return data;
    }
}