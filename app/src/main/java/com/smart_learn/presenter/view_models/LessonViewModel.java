package com.smart_learn.presenter.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.smart_learn.core.services.LessonService;
import com.smart_learn.data.models.room.entities.Lesson;
import com.smart_learn.data.models.room.relationships.LessonWithJoinedInfo;
import com.smart_learn.data.repository.LessonRepository;

import java.util.List;

/** https://developer.android.com/codelabs/android-room-with-a-view#9 */
public class LessonViewModel extends AndroidViewModel {

    private final LessonService lessonService;

    public LessonViewModel(@NonNull Application application) {
        super(application);
        lessonService = new LessonService(application);
    }

    public LiveData<Lesson> getSampleLiveLesson(int lessonId) {
        return lessonService.getSampleLiveLesson(lessonId);
    }

    public LiveData<Lesson> getSampleLiveLesson(String lessonName) {
        return lessonService.getSampleLiveLesson(lessonName);
    }

    public LiveData<LessonWithJoinedInfo> getFullLiveLessonInfo(int lessonId) { return lessonService.getFullLiveLessonInfo(lessonId); }

    public LiveData<List<Lesson>> getAllLiveSampleLessons() { return lessonService.getAllLiveSampleLessons(); }

    public boolean checkIfLessonExist(String lessonName) {
        return lessonService.checkIfLessonExist(lessonName);
    }

    public void insert(Lesson lesson) { lessonService.insert(lesson); }

    public void update(Lesson lesson) { lessonService.update(lesson); }

    public void delete(Lesson lesson) { lessonService.delete(lesson); }

}
