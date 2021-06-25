package com.smart_learn.data.room.entities.helpers;

import androidx.room.ColumnInfo;

import lombok.Getter;
import lombok.Setter;

/**
 * Used to store info`s about document backup status. Any room entity will be a Firebase Firestore
 * document in order to create the backup.
 */
@Getter
@Setter
public class BackupStatus {

    // the ID of the backup document from the backup sub-collection from Firebase Firestore
    @ColumnInfo(name = "backup_document_id")
    private String backupDocumentId;

    // mark that current document is added and must be added in backup
    @ColumnInfo(name = "is_added")
    private boolean isAdded;

    // mark that current document is updated and must be modified in backup
    @ColumnInfo(name = "is_updated")
    private boolean isUpdated;

    // mark that current document is deleted and must be deleted from backup
    @ColumnInfo(name = "is_deleted")
    private boolean isDeleted;

    // mark that current document is synced with backup
    @ColumnInfo(name = "is_synced")
    private boolean isSynced;

    public BackupStatus(String backupDocumentId, boolean isAdded, boolean isUpdated, boolean isDeleted, boolean isSynced) {
        this.backupDocumentId = backupDocumentId;
        this.isAdded = isAdded;
        this.isUpdated = isUpdated;
        this.isDeleted = isDeleted;
        this.isSynced = isSynced;
    }

    public static BackupStatus getStandardAddedBackupStatus(){
        return new BackupStatus("",true,false,false,false);
    }
}
