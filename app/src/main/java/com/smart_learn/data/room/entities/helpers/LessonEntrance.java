package com.smart_learn.data.room.entities.helpers;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.entities.Statistics;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
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

    // this will be used for generating questions
    // (every lesson entrance will have an empty statistics by default)
    @NotNull
    @NonNull
    @Embedded
    private Statistics statistics = new Statistics();

    public LessonEntrance(String notes, boolean isSelected, BasicInfo basicInfo,
                          Integer fkLessonId, boolean isFavourite, String language,
                          ArrayList<Translation> translations) {
        super(notes, isSelected, basicInfo);
        this.fkLessonId = fkLessonId;
        this.isFavourite = isFavourite;
        this.language = language;
        this.translations = translations;
    }

    public boolean areContentsTheSame(LessonEntrance newItem){
        if(newItem == null){
            return false;
        }
        return super.areContentsTheSame(newItem) &&
                CoreUtilities.General.areObjectsTheSame(this.fkLessonId, newItem.getFkLessonId()) &&
                this.isFavourite == newItem.isFavourite() &&
                CoreUtilities.General.areObjectsTheSame(this.language, newItem.getLanguage()) &&
                Translation.areContentsTheSame(this.translations, newItem.getTranslations()) &&
                CoreUtilities.General.areObjectsTheSame(this.statistics, newItem.getStatistics());
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics == null ? new Statistics() : statistics;
    }
}
