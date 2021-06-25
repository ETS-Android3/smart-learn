package com.smart_learn.data.room.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.smart_learn.core.config.RoomConfig;
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
@Entity(tableName = RoomConfig.EXPRESSIONS_TABLE,
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
public class Expression extends LessonEntrance {

    @PrimaryKey(autoGenerate = true)
    private int expressionId;

    private String expression;

    public Expression(Integer fkNotificationId, String notes, boolean isReceived, boolean isSelected,
                      DocumentMetadata documentMetadata, Integer fkLessonId, boolean isFavourite, String language,
                      ArrayList<Translation> translations, String expression) {
        super(fkNotificationId, notes, isReceived, isSelected, documentMetadata, fkLessonId, isFavourite,
                language, translations);
        this.expression = expression;
    }
}
