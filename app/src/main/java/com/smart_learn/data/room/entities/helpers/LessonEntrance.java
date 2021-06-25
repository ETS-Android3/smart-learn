package com.smart_learn.data.room.entities.helpers;

import androidx.room.ColumnInfo;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class LessonEntrance extends NotebookCommon {

    // use Integer because in some situations a null value for foreign key is needed
    @ColumnInfo(name = "fk_lesson_id")
    private Integer fkLessonId;

    @ColumnInfo(name = "is_favourite")
    private boolean isFavourite;

    @ColumnInfo(name = "language")
    private String language;

    // this will be actioned by type converter
    @ColumnInfo(name = "translations")
    protected ArrayList<Translation> translations;

    public LessonEntrance(Integer fkNotificationId, String notes, boolean isReceived, boolean isSelected,
                          DocumentMetadata documentMetadata, Integer fkLessonId, boolean isFavourite,
                          String language, ArrayList<Translation> translations) {
        super(fkNotificationId, notes, isReceived, isSelected, documentMetadata);
        this.fkLessonId = fkLessonId;
        this.isFavourite = isFavourite;
        this.language = language;
        this.translations = translations;
    }
}
