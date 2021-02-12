package com.smart_learn.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.smart_learn.models.room.entities.Lesson;
import com.smart_learn.models.room.relationships.LessonWithJoinedInfo;
import com.smart_learn.repository.LessonRepository;

import java.util.List;

/** https://developer.android.com/codelabs/android-room-with-a-view#9 */
public class LessonViewModel extends AndroidViewModel {

    private final LessonRepository repository;

    public LessonViewModel(@NonNull Application application) {
        super(application);
        repository = new LessonRepository(application);
    }

    public LiveData<Lesson> getSampleLiveLesson(int lessonId) {
        return repository.getSampleLiveLesson(lessonId);
    }

    public LiveData<Lesson> getSampleLiveLesson(String lessonName) {
        return repository.getSampleLiveLesson(lessonName);
    }

    public LiveData<LessonWithJoinedInfo> getFullLiveLessonInfo(int lessonId) { return repository.getFullLiveLessonInfo(lessonId); }

    public LiveData<List<Lesson>> getAllLiveSampleLessons() { return repository.getAllLiveSampleLessons(); }

    boolean checkIfLessonExist(String lessonName) {
        return repository.checkIfLessonExist(lessonName);
    }

    public void insert(Lesson lesson) { repository.insert(lesson); }

    public void update(Lesson lesson) { repository.update(lesson); }

    public void delete(Lesson lesson) { repository.delete(lesson); }

}
