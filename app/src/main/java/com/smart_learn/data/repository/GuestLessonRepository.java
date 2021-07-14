package com.smart_learn.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.smart_learn.data.room.dao.LessonDao;
import com.smart_learn.data.room.db.AppRoomDatabase;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.data.room.relationships.LessonWithJoinedInfo;
import com.smart_learn.data.room.repository.BasicRoomRepository;

import java.util.List;

/**
 * https://developer.android.com/codelabs/android-room-with-a-view#8
 * */
public class GuestLessonRepository extends BasicRoomRepository<Lesson, LessonDao> {

    private final LiveData<List<Lesson>> sampleLiveLessonList;

    public GuestLessonRepository(Application application) {
        // no need for db instance in class because communication will be made using dao interface
        super(AppRoomDatabase.getDatabaseInstance(application).lessonDao());

        // one query is enough because LiveData is made i.e. to be automatically notified by room
        // when changes are made in db
        sampleLiveLessonList = dao.getAllLiveSampleLessons();
    }

    /** Get a sample LiveData wrapped notebook based on lessonId. */
    public LiveData<Lesson> getSampleLiveLesson(int lessonId) {
        return dao.getSampleLiveLesson(lessonId);
    }

    /** Get a sample LiveData wrapped notebook based on lessonName. */
    public LiveData<Lesson> getSampleLiveLesson(String lessonName) {
        return dao.getSampleLiveLesson(lessonName);
    }


    /** Get all entries for a specific notebook. */
    public LiveData<LessonWithJoinedInfo> getFullLiveLessonInfo(int lessonId) {
        return dao.getFullLiveLessonInfo(lessonId);
    }

    /** Get a list of all lessons. */
    public LiveData<List<Lesson>> getAllLiveSampleLessons() {
        return sampleLiveLessonList;
    }

    /** Check if Lesson already exists in database. */
    public boolean checkIfLessonExist(String lessonName){
        return dao.getSampleLiveLesson(lessonName) == null;
    }

    public void updateAll(List<Lesson> items){
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            dao.updateAll(items);
        });
    }

    public void deleteAll(){
        AppRoomDatabase.databaseWriteExecutor.execute(dao::deleteAll);
    }

    public void deleteSelectedItems(){
        AppRoomDatabase.databaseWriteExecutor.execute(dao::deleteSelectedItems);
    }

    public void updateSelectAll(boolean isSelected){
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            dao.updateSelectAll(isSelected);
        });
    }

    public LiveData<Integer> getLiveSelectedItemsCount(){ return dao.getLiveSelectedItemsCount(); }

    public LiveData<Integer> getLiveItemsNumber(){ return dao.getLiveItemsNumber(); }

    public LiveData<Integer> getLiveNumberOfLessons(){
        return dao.getLiveNumberOfLessons();
    }
}
