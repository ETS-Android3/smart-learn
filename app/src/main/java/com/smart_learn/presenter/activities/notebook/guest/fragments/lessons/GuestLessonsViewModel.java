package com.smart_learn.presenter.activities.notebook.guest.fragments.lessons;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.core.services.GuestLessonService;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.data.room.entities.helpers.BasicInfo;
import com.smart_learn.presenter.helpers.adapters.lessons.GuestLessonsAdapter;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.lessons.LessonsViewModel;
import com.smart_learn.presenter.helpers.ApplicationController;

import org.jetbrains.annotations.NotNull;

public class GuestLessonsViewModel extends LessonsViewModel<GuestLessonsAdapter> {

    public GuestLessonsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    @Override
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
