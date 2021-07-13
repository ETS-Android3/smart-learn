package com.smart_learn.data.room.entities.helpers;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

import com.smart_learn.core.utilities.CoreUtilities;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.NonNull;
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
    private final BasicInfo basicInfo;

    public NotebookCommon(String notes, boolean isSelected, @NonNull @NotNull BasicInfo basicInfo) {
        this.notes = notes;
        this.isSelected = isSelected;
        this.basicInfo = basicInfo;
    }

    public boolean areContentsTheSame(NotebookCommon newItem){
        if(newItem == null){
            return false;
        }
        return CoreUtilities.General.areObjectsTheSame(this.notes, newItem.getNotes()) &&
                this.isSelected == newItem.isSelected() &&
                basicInfo.areContentsTheSame(newItem.getBasicInfo());
    }
}

