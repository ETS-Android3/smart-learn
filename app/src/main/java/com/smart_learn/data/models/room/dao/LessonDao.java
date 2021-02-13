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

    @Query("SELECT * FROM " + RoomConfig.LESSONS_TABLE + " WHERE lessonId = :lessonId")
    Lesson getSampleLesson(int lessonId);

    @Query("SELECT * FROM " + RoomConfig.LESSONS_TABLE + " WHERE name LIKE :lessonName")
    Lesson getSampleLesson(String lessonName);


    /** Get all Lesson info`s (with objects relationship data).
     * This will make a join for objects relationship between Lesson and 'relationship object'
     * info`s from Lesson. */
    @Transaction
    @Query("SELECT * FROM " + RoomConfig.LESSONS_TABLE + " WHERE lessonId = :lessonId")
    LiveData<LessonWithJoinedInfo> getFullLiveLessonInfo(int lessonId);


    /** Get only Lessons info`s without objects relationship data. */
    @Query("SELECT * FROM " + RoomConfig.LESSONS_TABLE)
    LiveData<List<Lesson>> getAllLiveSampleLessons();
}
