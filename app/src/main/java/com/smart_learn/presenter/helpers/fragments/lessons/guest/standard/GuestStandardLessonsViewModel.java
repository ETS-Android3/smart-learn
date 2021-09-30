package com.smart_learn.presenter.helpers.fragments.lessons.guest.standard;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.core.services.lesson.GuestLessonService;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.data.room.entities.helpers.BasicInfo;
import com.smart_learn.core.helpers.ApplicationController;
import com.smart_learn.presenter.helpers.fragments.lessons.guest.GuestBasicLessonsViewModel;

import org.jetbrains.annotations.NotNull;

public abstract class GuestStandardLessonsViewModel extends GuestBasicLessonsViewModel {

    public GuestStandardLessonsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public void addLessonByName(@NonNull @NotNull String lessonName) {
        Lesson lesson = new Lesson("", false, new BasicInfo(System.currentTimeMillis()), lessonName);
        GuestLessonService.getInstance().insert(lesson, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.success_adding_lesson));
            }

            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_adding_lesson));
            }
        });
    }

}
