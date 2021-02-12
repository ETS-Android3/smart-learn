package com.smart_learn.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.smart_learn.models.room.entities.Lesson;
import com.smart_learn.models.room.dao.LessonDao;
import com.smart_learn.models.room.db.AppRoomDatabase;
import com.smart_learn.models.room.relationships.LessonWithJoinedInfo;

import java.util.List;

/**
 * https://developer.android.com/codelabs/android-room-with-a-view#8
 * */
public class LessonRepository {

    private final LessonDao lessonDao;
    private final LiveData<List<Lesson>> sampleLessonList;

    public LessonRepository(Application application) {
        // no need for db instance in class because communication will be made using dao interface
        AppRoomDatabase db = AppRoomDatabase.getDatabaseInstance(application);

        // this is used to communicate with db
        lessonDao = db.lessonDao();

        // one query is enough because LiveData is made i.e. to be automatically notified by room
        // when changes are made in db
        sampleLessonList = lessonDao.getAllSampleLessons();
    }

    public LiveData<Lesson> getSampleLesson(int lessonId) {
        return lessonDao.getSampleLesson(lessonId);
    }

    public LiveData<LessonWithJoinedInfo> getFullLesson(int lessonId) {
        return lessonDao.getFullLessonInfo(lessonId);
    }

    public LiveData<List<Lesson>> getAllSampleLessons() {
        return sampleLessonList;
    }

    public void insert(Lesson lesson) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            lessonDao.insert(lesson);
        });
    }

    public void update(Lesson lesson) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            lessonDao.update(lesson);
        });
    }

    public void delete(Lesson lesson) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            lessonDao.delete(lesson);
        });
    }
}
