package com.smart_learn.data.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.smart_learn.core.helpers.CoreUtilities;
import com.smart_learn.data.room.db.AppRoomDatabase;
import com.smart_learn.data.room.entities.helpers.BasicInfo;
import com.smart_learn.data.room.entities.helpers.LessonEntrance;
import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.presenter.helpers.PresenterHelpers;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Leave the ForeignKey declaration in @Entity. If you put declaration on field propriety inside
 * the class constraint will not work.
 * */
@Getter
@Setter
@ToString
@Entity(tableName = AppRoomDatabase.WORDS_TABLE,
        foreignKeys = {
                    @ForeignKey(entity = Lesson.class,
                                parentColumns = "id",
                                childColumns = "fk_lesson_id",
                                onDelete = ForeignKey.CASCADE)
        })
public class Word extends LessonEntrance implements PresenterHelpers.SelectionHelper, PresenterHelpers.DiffUtilCallbackHelper<Word> {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int wordId;

    @ColumnInfo(name = "word")
    private String word;

    @ColumnInfo(name = "phonetic")
    private String phonetic;

    public Word(String notes, boolean isSelected, BasicInfo basicInfo, Integer fkLessonId,
                boolean isFavourite, String language, ArrayList<Translation> translations, String word, String phonetic) {
        super(notes, isSelected, basicInfo, fkLessonId, isFavourite, language, translations);
        this.word = word;
        this.phonetic = phonetic;
    }

    @Override
    public boolean areItemsTheSame(Word newItem) {
        if(newItem == null){
            return false;
        }
        return this.wordId == newItem.getWordId();
    }

    @Override
    public boolean areContentsTheSame(Word newItem){
        if(newItem == null){
            return false;
        }
        return super.areContentsTheSame(newItem) &&
                CoreUtilities.General.areObjectsTheSame(this.word, newItem.getWord()) &&
                CoreUtilities.General.areObjectsTheSame(this.phonetic, newItem.getPhonetic());
    }

    @Override
    public int getId() {
        return wordId;
    }

    public static Word generateEmptyObject(){
        return new Word("", false, BasicInfo.generateEmptyObject(), null,
                false, "", new ArrayList<>(), "", "");
    }

}
