package com.smart_learn.data.room.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.smart_learn.core.config.RoomConfig;
import com.smart_learn.data.room.entities.helpers.LessonEntrance;
import com.smart_learn.data.room.entities.helpers.Translation;

import lombok.Getter;
import lombok.Setter;

/**
 * Leave the ForeignKey declaration in @Entity. If you put declaration on field propriety inside
 * the class constraint will not work.
 * */
@Getter
@Setter
@Entity(tableName = RoomConfig.EXPRESSIONS_TABLE,
        foreignKeys = @ForeignKey(
                entity = Lesson.class,
                parentColumns = "lessonId",
                childColumns = "fkLessonId",
                onDelete = ForeignKey.CASCADE
        )
)
public class Expression extends LessonEntrance {

    @PrimaryKey(autoGenerate = true)
    private int expressionId;

    private String expression;

    public Expression(long createdAt, long modifiedAt, int fkLessonId, boolean isSelected, Translation translation, String expression) {
        super(createdAt, modifiedAt, fkLessonId, isSelected, translation);
        this.expression = expression;
    }
}
