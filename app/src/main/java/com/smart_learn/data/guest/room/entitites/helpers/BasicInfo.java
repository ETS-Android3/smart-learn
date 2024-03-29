package com.smart_learn.data.guest.room.entitites.helpers;

import androidx.room.ColumnInfo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
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

    public boolean areContentsTheSame(BasicInfo newItem){
        if(newItem == null){
            return false;
        }
        return this.createdAt == newItem.getCreatedAt() &&
                this.modifiedAt == newItem.getModifiedAt();
    }

    public static BasicInfo generateEmptyObject(){
        return new BasicInfo(System.currentTimeMillis());
    }
}

