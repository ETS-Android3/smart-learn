package com.smart_learn.data.guest.repository;

import androidx.lifecycle.LiveData;

import com.smart_learn.data.guest.room.dao.LessonDao;
import com.smart_learn.data.guest.room.db.AppRoomDatabase;
import com.smart_learn.data.guest.room.entitites.Lesson;
import com.smart_learn.data.guest.repository.helpers.BasicRoomRepository;
import com.smart_learn.core.common.helpers.ApplicationController;

import java.util.List;

/**
 * https://developer.android.com/codelabs/android-room-with-a-view#8
 * */
public class GuestLessonRepository extends BasicRoomRepository<Lesson, LessonDao> {

    private static GuestLessonRepository instance;

    private final LiveData<List<Lesson>> sampleLiveLessonList;

    private GuestLessonRepository() {
        // no need for db instance in class because communication will be made using dao interface
        super(AppRoomDatabase.getDatabaseInstance(ApplicationController.getInstance()).lessonDao());

        // one query is enough because LiveData is made i.e. to be automatically notified by room
        // when changes are made in db
        sampleLiveLessonList = dao.getAllLiveSampleLessons();
    }

    public static GuestLessonRepository getInstance() {
        if(instance == null){
            instance = new GuestLessonRepository();
        }
        return instance;
    }


    public LiveData<Lesson> getSampleLiveLesson(int lessonId) {
        return dao.getSampleLiveLesson(lessonId);
    }

    public LiveData<List<Lesson>> getAllLiveSampleLessons() {
        return sampleLiveLessonList;
    }

    public LiveData<Integer> getLiveNumberOfLessons(){
        return dao.getLiveNumberOfLessons();
    }
}
