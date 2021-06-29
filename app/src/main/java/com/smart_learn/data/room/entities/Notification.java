package com.smart_learn.data.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.smart_learn.data.room.db.AppRoomDatabase;
import com.smart_learn.data.room.entities.helpers.DocumentMetadata;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = AppRoomDatabase.NOTIFICATIONS_TABLE,
        foreignKeys = {
                        @ForeignKey(entity = Friend.class,
                                    parentColumns = "id",
                                    childColumns = "fk_friend_id",
                                    onDelete = ForeignKey.CASCADE)
        })
public class Notification {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int notificationId;

    @ColumnInfo(name = "fk_friend_id")
    private Integer fkFriendId;

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
    private final DocumentMetadata documentMetadata;

    public Notification(Integer fkFriendId, String from, int type, String description, boolean isMarkedAsRead,
                        boolean isHidden, DocumentMetadata documentMetadata) {
        this.fkFriendId = fkFriendId;
        this.from = from;
        this.type = type;
        this.description = description;
        this.isMarkedAsRead = isMarkedAsRead;
        this.isHidden = isHidden;
        this.documentMetadata = documentMetadata;
    }
}
