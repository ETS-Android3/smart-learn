package com.smart_learn.data.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.smart_learn.data.room.db.AppRoomDatabase;
import com.smart_learn.data.room.entities.helpers.DocumentMetadata;
import com.smart_learn.data.room.entities.helpers.LessonEntrance;
import com.smart_learn.data.room.entities.helpers.Translation;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

/**
 * Leave the ForeignKey declaration in @Entity. If you put declaration on field propriety inside
 * the class constraint will not work.
 * */
@Getter
@Setter
@Entity(tableName = AppRoomDatabase.EXPRESSIONS_TABLE,
        foreignKeys = {
                        @ForeignKey(entity = Lesson.class,
                                parentColumns = "id",
                                childColumns = "fk_lesson_id",
                                onDelete = ForeignKey.CASCADE)
        })
public class Expression extends LessonEntrance {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int expressionId;

    @ColumnInfo(name = "expression")
    private String expression;

    public Expression(String notes, boolean isSelected, DocumentMetadata documentMetadata, Integer fkLessonId,
                      boolean isFavourite, String language, ArrayList<Translation> translations, String expression) {
        super(notes, isSelected, documentMetadata, fkLessonId, isFavourite, language, translations);
        this.expression = expression;
    }
}
