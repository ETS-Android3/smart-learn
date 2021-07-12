package com.smart_learn.data.room.entities.helpers;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

import lombok.Getter;
import lombok.Setter;


/**
 * Used to store general info`s about documents. Any room entity will be a Firebase Firestore
 * document in order to create the backup.
 */
@Getter
@Setter
public class DocumentMetadata {

    // will be the user UID (UID from Firebase Authentication)
    @ColumnInfo(name = "owner")
    private final String owner;

    // timestamp to store when a document is created
    @ColumnInfo(name = "created_at")
    private final long createdAt;

    @ColumnInfo(name = "modified_at")
    // timestamp to store when a document was modified last time
    private long modifiedAt;

    public DocumentMetadata(String owner, long createdAt, long modifiedAt) {
        this.owner = owner;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}

