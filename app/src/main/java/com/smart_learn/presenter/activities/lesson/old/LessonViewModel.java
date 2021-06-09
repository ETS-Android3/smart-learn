package com.smart_learn.presenter.activities.lesson.old;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.smart_learn.data.models.room.entities.Lesson;

public class LessonViewModel extends BasicLessonViewModel {

    private LiveData<Lesson> liveCurrentLesson;

    public LessonViewModel(@NonNull Application application) {
        super(application);
    }

    public void setLiveCurrentLesson(long lessonId) {
        liveCurrentLesson = lessonService.getSampleLiveLesson(lessonId);
    }

    public LiveData<Lesson> getLiveCurrentLesson() {return liveCurrentLesson;}

    public void deleteCurrentLesson(){
        if(liveCurrentLesson.getValue() != null){
            lessonService.delete(liveCurrentLesson.getValue());
        }
    }
}

