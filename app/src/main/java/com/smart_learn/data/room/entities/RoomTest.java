package com.smart_learn.data.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.smart_learn.data.entities.Test;
import com.smart_learn.data.helpers.DataHelpers;
import com.smart_learn.data.room.db.AppRoomDatabase;
import com.smart_learn.data.room.entities.helpers.BasicInfo;
import com.smart_learn.presenter.helpers.PresenterHelpers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity(tableName = AppRoomDatabase.TESTS_TABLE)
public class RoomTest extends Test implements DataHelpers.RoomBasicInfoHelper, PresenterHelpers.DiffUtilCallbackHelper<RoomTest>, PresenterHelpers.SelectionHelper {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int testId;

    @Embedded
    private final BasicInfo basicInfo;

    public RoomTest(BasicInfo basicInfo) {
        super();
        this.basicInfo = basicInfo;
    }

    @Override
    public boolean areItemsTheSame(RoomTest newItem) {
        if(newItem == null){
            return false;
        }
        return this.testId == newItem.getTestId();
    }

    @Override
    public boolean areContentsTheSame(RoomTest newItem) {
        if(newItem == null){
            return false;
        }
        return super.equals(newItem) &&
                basicInfo.areContentsTheSame(newItem.getBasicInfo());
    }

    public static RoomTest generateEmptyObject(){
        return new RoomTest(BasicInfo.generateEmptyObject());
    }

    @Override
    public int getId() {
        return testId;
    }
}
