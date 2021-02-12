package com.smart_learn.models.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.smart_learn.config.RoomConfig;
import com.smart_learn.models.room.entities.Lesson;
import com.smart_learn.models.room.relationships.LessonWithJoinedInfo;

import java.util.List;

@Dao
public interface LessonDao extends BasicDao<Lesson> {

    /** Get only Lesson info`s without objects relationship data. */
    @Query("SELECT * FROM " + RoomConfig.LESSONS_TABLE + " WHERE lessonId = :lessonId")
    LiveData<Lesson> getSampleLesson(int lessonId);


    /** Get all Lesson info`s (with objects relationship data).
     * This will make a join for objects relationship between Lesson and 'relationship object'
     * info`s from Lesson. */
    @Transaction
    @Query("SELECT * FROM " + RoomConfig.LESSONS_TABLE + " WHERE lessonId = :lessonId")
    LiveData<LessonWithJoinedInfo> getFullLessonInfo(int lessonId);


    /** Get only Lessons info`s without objects relationship data. */
    @Query("SELECT * FROM " + RoomConfig.LESSONS_TABLE)
    LiveData<List<Lesson>> getAllSampleLessons();
}
