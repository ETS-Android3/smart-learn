package com.smart_learn.data.room.entities;

import android.text.Html;
import android.text.Spanned;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.smart_learn.core.config.RoomConfig;
import com.smart_learn.data.room.entities.helpers.IndexRange;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity(tableName = RoomConfig.LESSONS_TABLE)
public class Lesson {

    @PrimaryKey(autoGenerate = true)
    private int lessonId;

    private String name;
    private long createdAt;
    private long modifiedAt;
    private boolean isSelected; // helper for recycler view

    @Ignore
    // this indexes are used for search value in recycler view for making the foreground color
    private List<IndexRange> searchIndexes = new ArrayList<>();

    @Ignore
    // this will be used for showing the foreground color using html tags for text between searchIndexes
    private Spanned spannedName;

    public Lesson(String name, long createdAt, long modifiedAt, boolean isSelected) {
        this.name = name;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.isSelected = isSelected;
        this.spannedName = Html.fromHtml(this.name,Html.FROM_HTML_MODE_LEGACY);
    }

    public void addIndexRange(IndexRange indexRange){
        searchIndexes.add(indexRange);
    }

    public void setSpannedName(Spanned spannedName) { this.spannedName = spannedName; }

    public void resetSpannedName(){ spannedName = Html.fromHtml(this.name,Html.FROM_HTML_MODE_LEGACY); }

    public Spanned getSpannedName() { return spannedName; }
}
