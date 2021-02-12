package com.smart_learn.models.room.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.smart_learn.config.RoomConfig;
import com.smart_learn.models.room.entities.helpers.LessonEntrance;
import com.smart_learn.models.room.entities.helpers.Translation;

import lombok.Getter;
import lombok.Setter;

/**
 * Leave the ForeignKey declaration in @Entity. If you put declaration on field propriety inside
 * the class constraint will not work.
 * */
@Getter
@Setter
@Entity(tableName = RoomConfig.SENTENCES_TABLE,
        foreignKeys = @ForeignKey(
                entity = Lesson.class,
                parentColumns = "lessonId",
                childColumns = "fkLessonId",
                onDelete = ForeignKey.CASCADE
        )
)
public class Sentence extends LessonEntrance {

    @PrimaryKey(autoGenerate = true)
    private long sentenceId;

    private String sentence;

    public Sentence(long createdAt, long modifiedAt, long fkLessonId, Translation translation, String sentence) {
        super(createdAt, modifiedAt, fkLessonId, translation);
        this.sentence = sentence;
    }
}
