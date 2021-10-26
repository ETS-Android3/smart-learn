package com.smart_learn.presenter.guest.fragments.common.lessons.standard;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.core.guest.services.GuestLessonService;
import com.smart_learn.data.common.helpers.DataCallbacks;
import com.smart_learn.data.guest.room.entitites.Lesson;
import com.smart_learn.data.guest.room.entitites.helpers.BasicInfo;
import com.smart_learn.core.common.helpers.ApplicationController;
import com.smart_learn.presenter.guest.fragments.common.lessons.GuestBasicLessonsViewModel;

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
