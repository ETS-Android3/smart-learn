package com.smart_learn.presenter.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.data.models.room.entities.Lesson;

import lombok.Getter;

public class LessonDialogViewModel extends LessonViewModel {

    @Getter
    private final MutableLiveData<Lesson> liveLessonInfo;

    public LessonDialogViewModel(@NonNull Application application) {
        super(application);
        // this should be initialized in order to avoid null on getValue for live data
        liveLessonInfo = new MutableLiveData<>(new Lesson("",0,0,false));
    }

    public void setLiveLessonInfo(Lesson lesson){
        liveLessonInfo.setValue(lesson);
    }
}
