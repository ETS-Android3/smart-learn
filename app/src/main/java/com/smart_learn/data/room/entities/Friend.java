package com.smart_learn.data.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.smart_learn.data.room.db.AppRoomDatabase;
import com.smart_learn.data.room.entities.helpers.DocumentMetadata;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = AppRoomDatabase.FRIENDS_TABLE)
public class Friend {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int friendId;

    @ColumnInfo(name = "profile_name")
    private String profileName;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "photo_url")
    private String photoUrl;

    @ColumnInfo(name = "account_created_at")
    private long accountCreatedAt;

    @ColumnInfo(name = "is_pending")
    boolean isPending;

    @Embedded
    private final DocumentMetadata documentMetadata;

    public Friend(String profileName, String email, String photoUrl, long accountCreatedAt, boolean isPending,
                  DocumentMetadata documentMetadata) {
        this.profileName = profileName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.accountCreatedAt = accountCreatedAt;
        this.isPending = isPending;
        this.documentMetadata = documentMetadata;
    }
}

