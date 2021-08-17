package com.smart_learn.data.firebase.firestore.entities.helpers;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class BasicProfileDocument {

    public interface Fields {
        String EMAIL_FIELD_NAME = "email";
        String DISPLAY_NAME_FIELD_NAME = "displayName";
        String PROFILE_PHOTO_URL_FIELD_NAME = "profilePhotoUrl";
        String IS_ACCOUNT_MARKED_FOR_DELETION_FIELD_NAME = "accountMarkedForDeletion";
    }

    private DocumentMetadata documentMetadata;

    private String email;
    private String displayName;
    private String profilePhotoUrl;
    // mark that associated user deleted his account
    private boolean isAccountMarkedForDeletion;

    public BasicProfileDocument() {
        // needed for Firestore
    }

    public BasicProfileDocument(DocumentMetadata documentMetadata, String email, String displayName, String profilePhotoUrl) {
        this.documentMetadata = documentMetadata;
        this.email = email;
        this.displayName = displayName;
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public static HashMap<String, Object> convertDocumentToHashMap(BasicProfileDocument document){
        if(document == null){
            return new HashMap<>();
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put(DocumentMetadata.Fields.DOCUMENT_METADATA_FIELD_NAME, DocumentMetadata.convertDocumentToHashMap(document.getDocumentMetadata()));
        data.put(BasicProfileDocument.Fields.EMAIL_FIELD_NAME, document.getEmail());
        data.put(BasicProfileDocument.Fields.DISPLAY_NAME_FIELD_NAME, document.getDisplayName());
        data.put(BasicProfileDocument.Fields.PROFILE_PHOTO_URL_FIELD_NAME, document.getProfilePhotoUrl());
        data.put(Fields.IS_ACCOUNT_MARKED_FOR_DELETION_FIELD_NAME, document.isAccountMarkedForDeletion());

        return data;
    }
}