package com.smart_learn.data.room.entities;

import android.text.Html;
import android.text.Spanned;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.smart_learn.data.room.db.AppRoomDatabase;
import com.smart_learn.data.room.entities.helpers.DocumentMetadata;
import com.smart_learn.data.room.entities.helpers.IndexRange;
import com.smart_learn.data.room.entities.helpers.LessonEntrance;
import com.smart_learn.data.room.entities.helpers.Translation;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Leave the ForeignKey declaration in @Entity. If you put declaration on field propriety inside
 * the class constraint will not work.
 * */
@Getter
@Setter
@Entity(tableName = AppRoomDatabase.WORDS_TABLE,
        foreignKeys = {
                    @ForeignKey(entity = Notification.class,
                                parentColumns = "id",
                                childColumns = "fk_notification_id",
                                onDelete = ForeignKey.CASCADE),
                    @ForeignKey(entity = Lesson.class,
                                parentColumns = "lessonId",
                                childColumns = "fkLessonId",
                                onDelete = ForeignKey.CASCADE)
        })
public class Word extends LessonEntrance {

    @PrimaryKey(autoGenerate = true)
    private int wordId;

    private String word;

    @Ignore
    // this indexes are used for search value in recycler view for making the foreground color
    private List<IndexRange> searchIndexes = new ArrayList<>();

    @Ignore
    // this will be used for showing the foreground color using html tags for text between searchIndexes
    private Spanned spannedWord;

    public Word(Integer fkNotificationId, String notes, boolean isReceived, boolean isSelected,
                DocumentMetadata documentMetadata, Integer fkLessonId, boolean isFavourite, String language,
                ArrayList<Translation> translations, String word) {
        super(fkNotificationId, notes, isReceived, isSelected, documentMetadata, fkLessonId, isFavourite,
                language, translations);
        this.word = word;
    }

    public void addIndexRange(IndexRange indexRange){
        searchIndexes.add(indexRange);
    }

    public void setSpannedWord(Spanned spannedWord) { this.spannedWord = spannedWord; }

    public void resetSpannedWord(){ spannedWord = Html.fromHtml(this.word,Html.FROM_HTML_MODE_LEGACY); }

    public Spanned getSpannedWord() { return spannedWord; }
}
