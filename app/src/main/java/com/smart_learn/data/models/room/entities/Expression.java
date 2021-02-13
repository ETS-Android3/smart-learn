package com.smart_learn.data.models.room.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.smart_learn.core.config.RoomConfig;
import com.smart_learn.data.models.room.entities.helpers.LessonEntrance;
import com.smart_learn.data.models.room.entities.helpers.Translation;

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
    private long expressionId;

    private String expression;

    public Expression(long createdAt, long modifiedAt, long fkLessonId, Translation translation, String expression) {
        super(createdAt, modifiedAt, fkLessonId, translation);
        this.expression = expression;
    }
}
