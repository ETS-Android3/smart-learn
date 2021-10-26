package com.smart_learn.data.user.firebase.firestore.entities;

import com.smart_learn.data.user.firebase.firestore.entities.helpers.DocumentMetadata;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GroupChatMessageDocument {

    // At boolean fields if 'is' appears in front leave fields without 'is'  in
    // order to work with Firestore.
    public interface Fields {
        String MESSAGE_FIELD_NAME = "message";
        String FROM_USER_DISPLAY_NAME_FIELD_NAME = "fromUserDisplayName";
        String FROM_USER_EMAIL_FIELD_NAME = "fromUserEmail";
        String FROM_USER_PROFILE_PHOTO_URL_FIELD_NAME = "fromUserProfilePhotoUrl";
    }

    private DocumentMetadata documentMetadata;
    private String message = "";

    // used for a faster access at user data
    private String fromUserDisplayName = "";
    private String fromUserEmail = "";
    private String fromUserProfilePhotoUrl = "";

    public GroupChatMessageDocument() {
        // needed for Firestore
    }

    public GroupChatMessageDocument(DocumentMetadata documentMetadata, String message, String fromUserDisplayName,
                                    String fromUserEmail, String fromUserProfilePhotoUrl) {
        this.documentMetadata = documentMetadata;
        this.message = message;
        this.fromUserDisplayName = fromUserDisplayName;
        this.fromUserEmail = fromUserEmail;
        this.fromUserProfilePhotoUrl = fromUserProfilePhotoUrl;
    }

    public void setMessage(String message) {
        this.message = message == null ? "" : message;
    }

    public void setFromUserDisplayName(String fromUserDisplayName) {
        this.fromUserDisplayName = fromUserDisplayName == null ? "" : fromUserDisplayName;
    }

    public void setFromUserEmail(String fromUserEmail) {
        this.fromUserEmail = fromUserEmail == null ? "" : fromUserEmail;
    }

    public void setFromUserProfilePhotoUrl(String fromUserProfilePhotoUrl) {
        this.fromUserProfilePhotoUrl = fromUserProfilePhotoUrl == null ? "" : fromUserProfilePhotoUrl;
    }

    public static HashMap<String, Object> convertDocumentToHashMap(GroupChatMessageDocument groupChatMessageDocument){
        if(groupChatMessageDocument == null){
            return new HashMap<>();
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put(DocumentMetadata.Fields.DOCUMENT_METADATA_FIELD_NAME, DocumentMetadata.convertDocumentToHashMap(groupChatMessageDocument.getDocumentMetadata()));
        data.put(Fields.MESSAGE_FIELD_NAME, groupChatMessageDocument.getMessage());
        data.put(Fields.FROM_USER_DISPLAY_NAME_FIELD_NAME, groupChatMessageDocument.getFromUserDisplayName());
        data.put(Fields.FROM_USER_EMAIL_FIELD_NAME, groupChatMessageDocument.getFromUserEmail());
        data.put(Fields.FROM_USER_PROFILE_PHOTO_URL_FIELD_NAME, groupChatMessageDocument.getFromUserProfilePhotoUrl());

        return data;
    }
}
