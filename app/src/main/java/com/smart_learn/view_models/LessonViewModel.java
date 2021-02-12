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

    public LiveData<Lesson> getSampleLesson(int lessonId) {
        return repository.getSampleLesson(lessonId);
    }

    public LiveData<LessonWithJoinedInfo> getFullLesson(int lessonId) { return repository.getFullLesson(lessonId); }

    public LiveData<List<Lesson>> getAllSampleLessons() { return repository.getAllSampleLessons(); }

    public void insert(Lesson lesson) { repository.insert(lesson); }

    public void update(Lesson lesson) { repository.update(lesson); }

    public void delete(Lesson lesson) { repository.delete(lesson); }

}
