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

    @ColumnInfo(name = "notes")
    private String notes;

    // mark that a document is selected or not in recycler view
    @ColumnInfo(name = "isSelected")
    private boolean isSelected;

    @Embedded
    private final DocumentMetadata documentMetadata;

    public NotebookCommon(String notes, boolean isSelected, DocumentMetadata documentMetadata) {
        this.notes = notes;
        this.isSelected = isSelected;
        this.documentMetadata = documentMetadata;
    }
}

