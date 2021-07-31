package com.smart_learn.data.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.smart_learn.data.room.db.AppRoomDatabase;
import com.smart_learn.data.room.entities.RoomTest;

import java.util.List;

@Dao
public interface RoomTestDao extends BasicDao<RoomTest> {

    @Query("SELECT * FROM " + AppRoomDatabase.TESTS_TABLE + " WHERE isScheduled AND isHidden == 0")
    LiveData<List<RoomTest>> getAllLiveNotHiddenScheduledTests();

    @Query("SELECT * FROM " + AppRoomDatabase.TESTS_TABLE + " WHERE isScheduled == 0 AND isHidden == 0")
    LiveData<List<RoomTest>> getAllLiveNotHiddenNonScheduledTests();

    @Query("SELECT * FROM " + AppRoomDatabase.TESTS_TABLE + " WHERE isScheduled == 0 AND isFinished == 0 AND isHidden == 0")
    LiveData<List<RoomTest>> getAllLiveNotHiddenInProgressTests();

    @Query("SELECT * FROM " + AppRoomDatabase.TESTS_TABLE + " WHERE isScheduled == 0 AND isFinished AND isHidden == 0")
    LiveData<List<RoomTest>> getAllLiveNotHiddenFinishedTests();

    @Query("SELECT * FROM " + AppRoomDatabase.TESTS_TABLE + " WHERE id == :testId")
    LiveData<RoomTest> getLiveTest(int testId);

    @Query("SELECT * FROM " + AppRoomDatabase.TESTS_TABLE + " WHERE id == :testId")
    RoomTest getTest(int testId);

    @Query("SELECT COUNT(id) FROM " + AppRoomDatabase.TESTS_TABLE + " WHERE isScheduled == 0 AND isHidden == 0")
    LiveData<Integer> getLiveNumberOfNotHiddenNonScheduledTests();

    @Query("SELECT COUNT(id) FROM " + AppRoomDatabase.TESTS_TABLE + " WHERE isScheduled == 0")
    Integer getNumberOfNonScheduledTests();

    @Query("SELECT COUNT(id) FROM " + AppRoomDatabase.TESTS_TABLE + " WHERE isScheduled")
    Integer getNumberOfScheduledTests();
}
