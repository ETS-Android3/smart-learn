package com.smart_learn.data.firebase.firestore.entities.helpers;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.data.helpers.DataUtilities;

import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

@Getter
@Setter
public class DocumentMetadata {

    public interface Fields {
        String DOCUMENT_METADATA_FIELD_NAME = "documentMetadata";
        String OWNER_FIELD_NAME = "owner";
        String CREATED_AT_FIELD_NAME = "createdAt";
        String MODIFIED_AT_FIELD_NAME = "modifiedAt";
    }

    // will be the user UID (UID from Firebase Authentication)
    private String owner;
    // timestamp to store when a document is created
    private long createdAt;
    // timestamp to store when a document was modified last time
    private long modifiedAt;

    public DocumentMetadata() {
        // needed for Firestore
    }

    public DocumentMetadata(String owner, long createdAt) {
        this.owner = owner == null ? "" : owner;
        this.createdAt = createdAt;
        this.modifiedAt = createdAt;
    }

    public DocumentMetadata(String owner, long createdAt, long modifiedAt) {
        this.owner = owner == null ? "" : owner;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static boolean isGoodDocumentMetadataConfiguration(DocumentSnapshot documentSnapshot){
        if(!DataUtilities.Firestore.isGoodDocumentSnapshot(documentSnapshot)){
            Timber.w("documentSnapshot is not valid");
            return false;
        }

        // check if the provided documentSnapshot contains all fields from the document metadata document
        if(!documentSnapshot.contains(Fields.DOCUMENT_METADATA_FIELD_NAME + "." + Fields.OWNER_FIELD_NAME)){
            Timber.w("documentSnapshot does not contain field " + Fields.DOCUMENT_METADATA_FIELD_NAME + "." + Fields.OWNER_FIELD_NAME);
            return false;
        }

        if(!documentSnapshot.contains(Fields.DOCUMENT_METADATA_FIELD_NAME + "." + Fields.CREATED_AT_FIELD_NAME)){
            Timber.w("documentSnapshot does not contain field " + Fields.DOCUMENT_METADATA_FIELD_NAME + "." + Fields.CREATED_AT_FIELD_NAME);
            return false;
        }

        if(!documentSnapshot.contains(Fields.DOCUMENT_METADATA_FIELD_NAME + "." + Fields.MODIFIED_AT_FIELD_NAME)){
            Timber.w("documentSnapshot does not contain field " + Fields.DOCUMENT_METADATA_FIELD_NAME + "." + Fields.MODIFIED_AT_FIELD_NAME);
            return false;
        }

        return true;
    }
}
