package com.smart_learn.data.room.entities.helpers;

import androidx.room.Embedded;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class LessonEntrance {

    private final long createdAt;
    private long modifiedAt;
    private int fkLessonId;
    private boolean isSelected; // helper for recycler view

    /** Leave this embedded in order to be decomposed in room_db */
    @Embedded
    private Translation translation;

    public LessonEntrance(long createdAt, long modifiedAt, int fkLessonId, boolean isSelected, Translation translation) {
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.fkLessonId = fkLessonId;
        this.isSelected = isSelected;
        this.translation = translation;
    }

}
