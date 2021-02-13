package com.smart_learn.core.services;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.smart_learn.data.models.room.entities.Lesson;
import com.smart_learn.data.models.room.relationships.LessonWithJoinedInfo;
import com.smart_learn.data.repository.LessonRepository;

import java.util.List;

public class LessonService extends BasicRoomService<Lesson> {

    private final LessonRepository lessonRepository;

    public LessonService(Application application){
        lessonRepository = new LessonRepository(application);

        // set super repository
        super.basicRoomRepository = lessonRepository;
    }

    public LiveData<Lesson> getSampleLiveLesson(int lessonId) {
        return lessonRepository.getSampleLiveLesson(lessonId);
    }

    public LiveData<Lesson> getSampleLiveLesson(String lessonName) {
        return lessonRepository.getSampleLiveLesson(lessonName);
    }

    public LiveData<LessonWithJoinedInfo> getFullLiveLessonInfo(int lessonId) { return lessonRepository.getFullLiveLessonInfo(lessonId); }

    public LiveData<List<Lesson>> getAllLiveSampleLessons() { return lessonRepository.getAllLiveSampleLessons(); }

    public boolean checkIfLessonExist(String lessonName) {
        return lessonRepository.checkIfLessonExist(lessonName);
    }

}
