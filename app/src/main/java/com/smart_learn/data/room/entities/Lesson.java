package com.smart_learn.data.room.entities;

import android.text.Html;
import android.text.Spanned;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.smart_learn.data.room.db.AppRoomDatabase;
import com.smart_learn.data.room.entities.helpers.BasicInfo;
import com.smart_learn.data.room.entities.helpers.IndexRange;
import com.smart_learn.data.room.entities.helpers.NotebookCommon;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity(tableName = AppRoomDatabase.LESSONS_TABLE)
public class Lesson extends NotebookCommon {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int lessonId;

    @ColumnInfo(name = "name")
    private String name;

    @Ignore
    // this indexes are used for search value in recycler view for making the foreground color
    private List<IndexRange> searchIndexes = new ArrayList<>();

    @Ignore
    // this will be used for showing the foreground color using html tags for text between searchIndexes
    private Spanned spannedName;

    public Lesson(String notes, boolean isSelected, BasicInfo basicInfo, String name) {
        super(notes, isSelected, basicInfo);
        this.name = name;
        this.spannedName = Html.fromHtml(this.name,Html.FROM_HTML_MODE_LEGACY);
    }

    public void addIndexRange(IndexRange indexRange){
        searchIndexes.add(indexRange);
    }

    public void setSpannedName(Spanned spannedName) { this.spannedName = spannedName; }

    public void resetSpannedName(){ spannedName = Html.fromHtml(this.name,Html.FROM_HTML_MODE_LEGACY); }

    public Spanned getSpannedName() { return spannedName; }
}
