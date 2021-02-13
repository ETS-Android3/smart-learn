package com.smart_learn.data.models.room.entities.helpers;

import androidx.room.Embedded;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class LessonEntrance {

    private final long createdAt;
    private long modifiedAt;

    private long fkLessonId;

    /** Leave this embedded in order to be decomposed in room_db */
    @Embedded
    private Translation translation;

    public LessonEntrance(long createdAt, long modifiedAt, long fkLessonId, Translation translation) {
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.fkLessonId = fkLessonId;
        this.translation = translation;
    }
}
