package com.smart_learn.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.smart_learn.data.room.dao.LessonDao;
import com.smart_learn.data.room.db.AppRoomDatabase;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.data.room.relationships.LessonWithJoinedInfo;

import java.util.List;

/**
 * https://developer.android.com/codelabs/android-room-with-a-view#8
 * */
public class GuestLessonRepository extends BasicRoomRepository<Lesson> {

    private final LessonDao lessonDao;
    private final LiveData<List<Lesson>> sampleLiveLessonList;

    public GuestLessonRepository(Application application) {
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

    /** Get a sample LiveData wrapped notebook based on lessonId. */
    public LiveData<Lesson> getSampleLiveLesson(int lessonId) {
        return lessonDao.getSampleLiveLesson(lessonId);
    }

    /** Get a sample LiveData wrapped notebook based on lessonName. */
    public LiveData<Lesson> getSampleLiveLesson(String lessonName) {
        return lessonDao.getSampleLiveLesson(lessonName);
    }


    /** Get all entries for a specific notebook. */
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

    public void updateAll(List<Lesson> items){
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            lessonDao.updateAll(items);
        });
    }

    public void deleteAll(){
        AppRoomDatabase.databaseWriteExecutor.execute(lessonDao::deleteAll);
    }

    public void deleteSelectedItems(){
        AppRoomDatabase.databaseWriteExecutor.execute(lessonDao::deleteSelectedItems);
    }

    public void updateSelectAll(boolean isSelected){
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            lessonDao.updateSelectAll(isSelected);
        });
    }

    public LiveData<Integer> getLiveSelectedItemsCount(){ return lessonDao.getLiveSelectedItemsCount(); }

    public LiveData<Integer> getLiveItemsNumber(){ return lessonDao.getLiveItemsNumber(); }

    public LiveData<Integer> getLiveNumberOfLessons(){
        return lessonDao.getLiveNumberOfLessons();
    }
}
