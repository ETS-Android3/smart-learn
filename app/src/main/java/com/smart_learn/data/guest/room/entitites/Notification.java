package com.smart_learn.data.guest.room.entitites;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.smart_learn.data.guest.room.db.AppRoomDatabase;
import com.smart_learn.data.guest.room.entitites.helpers.BasicInfo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity(tableName = AppRoomDatabase.NOTIFICATIONS_TABLE)
public class Notification {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int notificationId;

    @ColumnInfo(name = "from")
    private String from;

    @ColumnInfo(name = "type")
    private int type;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "is_marked_as_read")
    private boolean isMarkedAsRead;

    @ColumnInfo(name = "is_hidden")
    private boolean isHidden;

    @Embedded
    private final BasicInfo basicInfo;

    public Notification(String from, int type, String description, boolean isMarkedAsRead,
                        boolean isHidden, BasicInfo basicInfo) {
        this.from = from;
        this.type = type;
        this.description = description;
        this.isMarkedAsRead = isMarkedAsRead;
        this.isHidden = isHidden;
        this.basicInfo = basicInfo;
    }
}
