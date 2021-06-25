package com.smart_learn.data.room.entities.helpers;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

import lombok.Getter;
import lombok.Setter;

/**
 * Use to store common info`s about Lessons and Lesson entries.
 * */
@Getter
@Setter
public abstract class NotebookCommon {

    // use Integer because in some situations a null value for foreign key is needed
    @ColumnInfo(name = "fk_notification_id")
    private Integer fkNotificationId;

    @ColumnInfo(name = "notes")
    private String notes;

    // mark that a document is received from notification
    @ColumnInfo(name = "is_received")
    private boolean isReceived;

    // mark that a document is selected or not in recycler view
    @ColumnInfo(name = "isSelected")
    private boolean isSelected;

    @Embedded
    private final DocumentMetadata documentMetadata;

    public NotebookCommon(Integer fkNotificationId, String notes, boolean isReceived, boolean isSelected,
                          DocumentMetadata documentMetadata) {
        this.fkNotificationId = fkNotificationId;
        this.notes = notes;
        this.isReceived = isReceived;
        this.isSelected = isSelected;
        this.documentMetadata = documentMetadata;
    }
}

