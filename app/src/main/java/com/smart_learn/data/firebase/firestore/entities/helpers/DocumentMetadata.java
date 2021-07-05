package com.smart_learn.data.firebase.firestore.entities.helpers;

import lombok.Getter;
import lombok.Setter;

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

}
