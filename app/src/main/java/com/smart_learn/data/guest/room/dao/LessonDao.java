package com.smart_learn.data.guest.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.smart_learn.data.guest.room.dao.helpers.BasicDao;
import com.smart_learn.data.guest.room.db.AppRoomDatabase;
import com.smart_learn.data.guest.room.entitites.Lesson;
import com.smart_learn.data.guest.room.relationships.LessonWithJoinedInfo;

import java.util.List;

@Dao
public interface LessonDao extends BasicDao<Lesson> {

    @Query("SELECT * FROM " + AppRoomDatabase.LESSONS_TABLE + " WHERE id == :lessonId")
    LiveData<Lesson> getSampleLiveLesson(int lessonId);

    /** Get all Lesson info`s (with objects relationship data).
     * This will make a join for objects relationship between Lesson and 'relationship object'
     * info`s from Lesson. */
    @Transaction
    @Query("SELECT * FROM " + AppRoomDatabase.LESSONS_TABLE + " WHERE id == :lessonId")
    LiveData<LessonWithJoinedInfo> getFullLiveLessonInfo(int lessonId);

    /** Get only Lessons info`s without objects relationship data. */
    @Query("SELECT * FROM " + AppRoomDatabase.LESSONS_TABLE)
    LiveData<List<Lesson>> getAllLiveSampleLessons();

    @Query("SELECT COUNT(id) FROM " + AppRoomDatabase.LESSONS_TABLE)
    LiveData<Integer> getLiveNumberOfLessons();
}
