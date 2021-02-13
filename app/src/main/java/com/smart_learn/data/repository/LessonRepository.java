package com.smart_learn.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.smart_learn.data.models.room.entities.Lesson;
import com.smart_learn.data.models.room.dao.LessonDao;
import com.smart_learn.data.models.room.db.AppRoomDatabase;
import com.smart_learn.data.models.room.relationships.LessonWithJoinedInfo;

import java.util.List;

/**
 * https://developer.android.com/codelabs/android-room-with-a-view#8
 * */
public class LessonRepository extends BasicRoomRepository<Lesson> {

    private final LessonDao lessonDao;
    private final LiveData<List<Lesson>> sampleLiveLessonList;

    public LessonRepository(Application application) {
        // no need for db instance in class because communication will be made using dao interface
        AppRoomDatabase db = AppRoomDatabase.getDatabaseInstance(application);

        // this is used to communicate with db
        lessonDao = db.lessonDao();

        // set dao in super class
        super.basicDao = lessonDao;

        // one query is enough because LiveData is made i.e. to be automatically notified by room
        // when changes are made in db
        sampleLiveLessonList = lessonDao.getAllLiveSampleLessons();
    }

    /** Get a sample LiveData wrapped lesson based on lessonId. */
    public LiveData<Lesson> getSampleLiveLesson(int lessonId) {
        return lessonDao.getSampleLiveLesson(lessonId);
    }

    /** Get a sample LiveData wrapped lesson based on lessonName. */
    public LiveData<Lesson> getSampleLiveLesson(String lessonName) {
        return lessonDao.getSampleLiveLesson(lessonName);
    }

    /** Get all entries for a specific lesson. */
    public LiveData<LessonWithJoinedInfo> getFullLiveLessonInfo(int lessonId) {
        return lessonDao.getFullLiveLessonInfo(lessonId);
    }

    /** Get a list of all lessons. */
    public LiveData<List<Lesson>> getAllLiveSampleLessons() {
        return sampleLiveLessonList;
    }

    /** Check if Lesson already exists in database. */
    public boolean checkIfLessonExist(String lessonName){
        return lessonDao.getSampleLiveLesson(lessonName) == null;
    }
}
