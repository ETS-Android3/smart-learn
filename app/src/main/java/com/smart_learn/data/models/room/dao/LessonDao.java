package com.smart_learn.data.models.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.smart_learn.core.config.RoomConfig;
import com.smart_learn.data.models.room.entities.Lesson;
import com.smart_learn.data.models.room.relationships.LessonWithJoinedInfo;

import java.util.List;

@Dao
public interface LessonDao extends BasicDao<Lesson> {

    @Query("SELECT * FROM " + RoomConfig.LESSONS_TABLE + " WHERE lessonId = :lessonId")
    LiveData<Lesson> getSampleLiveLesson(int lessonId);

    @Query("SELECT * FROM " + RoomConfig.LESSONS_TABLE + " WHERE name LIKE :lessonName")
    LiveData<Lesson> getSampleLiveLesson(String lessonName);

    /** Get all Lesson info`s (with objects relationship data).
     * This will make a join for objects relationship between Lesson and 'relationship object'
     * info`s from Lesson. */
    @Transaction
    @Query("SELECT * FROM " + RoomConfig.LESSONS_TABLE + " WHERE lessonId = :lessonId")
    LiveData<LessonWithJoinedInfo> getFullLiveLessonInfo(int lessonId);

    /** Get only Lessons info`s without objects relationship data. */
    @Query("SELECT * FROM " + RoomConfig.LESSONS_TABLE)
    LiveData<List<Lesson>> getAllLiveSampleLessons();

    @Query("DELETE FROM " + RoomConfig.LESSONS_TABLE)
    void deleteAll();

    @Query("DELETE FROM " + RoomConfig.LESSONS_TABLE + " WHERE isSelected")
    void deleteSelectedItems();

    @Query("UPDATE " + RoomConfig.LESSONS_TABLE + " set isSelected = :isSelected")
    void updateSelectAll(boolean isSelected);

    @Query("SELECT COUNT(lessonId) FROM " + RoomConfig.LESSONS_TABLE + " WHERE isSelected")
    LiveData<Integer> getLiveSelectedItemsCount();

    @Query("SELECT COUNT(lessonId) FROM " + RoomConfig.LESSONS_TABLE)
    LiveData<Integer> getLiveItemsNumber();
}
