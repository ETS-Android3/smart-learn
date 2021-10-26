package com.smart_learn.data.guest.room.entitites;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.smart_learn.core.common.helpers.CoreUtilities;
import com.smart_learn.data.guest.room.db.AppRoomDatabase;
import com.smart_learn.data.guest.room.entitites.helpers.BasicInfo;
import com.smart_learn.data.guest.room.entitites.helpers.NotebookCommon;
import com.smart_learn.presenter.common.helpers.PresenterHelpers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity(tableName = AppRoomDatabase.LESSONS_TABLE)
public class Lesson extends NotebookCommon implements PresenterHelpers.SelectionHelper, PresenterHelpers.DiffUtilCallbackHelper<Lesson> {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int lessonId;

    @ColumnInfo(name = "name")
    private String name;

    public Lesson(String notes, boolean isSelected, BasicInfo basicInfo, String name) {
        super(notes, isSelected, basicInfo);
        this.name = name;
    }

    @Override
    public boolean areItemsTheSame(Lesson newItem) {
        if(newItem == null){
            return false;
        }
        return this.lessonId == newItem.getLessonId();
    }

    @Override
    public boolean areContentsTheSame(Lesson newItem){
        if(newItem == null){
            return false;
        }
        return super.areContentsTheSame(newItem) &&
                CoreUtilities.General.areObjectsTheSame(this.name, newItem.getName());
    }

    @Override
    public int getId() {
        return lessonId;
    }

    public static Lesson generateEmptyObject(){
        return new Lesson("", false, BasicInfo.generateEmptyObject(), "");
    }
}
