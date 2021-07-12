package com.smart_learn.data.room.entities.helpers;

import androidx.room.ColumnInfo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BasicInfo {

    @ColumnInfo(name = "created_at")
    private final long createdAt;

    @ColumnInfo(name = "modified_at")
    private long modifiedAt;

    // Related to the Room constructor https://developer.android.com/reference/android/arch/persistence/room/Entity
    public BasicInfo(long createdAt) {
        this.createdAt = createdAt;
        this.modifiedAt = createdAt;
    }
}

