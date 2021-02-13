package com.smart_learn.data.models.room.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.smart_learn.config.RoomConfig;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity(tableName = RoomConfig.LESSONS_TABLE)
public class Lesson {

    @PrimaryKey(autoGenerate = true)
    private long lessonId;

    private String name;
    private final long createdAt;
    private long modifiedAt;

    public Lesson(String name, long createdAt, long modifiedAt) {
        this.name = name;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

}
