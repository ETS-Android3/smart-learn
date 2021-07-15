package com.smart_learn.presenter.activities.notebook.guest.fragments.home_lesson;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.R;
import com.smart_learn.core.services.GuestLessonService;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.home_lesson.HomeLessonViewModel;
import com.smart_learn.presenter.helpers.ApplicationController;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public class GuestHomeLessonViewModel extends HomeLessonViewModel {

    @Getter
    protected final MutableLiveData<Lesson> liveLesson;

    public GuestHomeLessonViewModel(@NonNull @NotNull Application application) {
        super(application);
        liveLesson = new MutableLiveData<>(Lesson.generateEmptyObject());
    }

    public void setLiveLesson(Lesson lesson){
        liveLesson.setValue(lesson);
        super.setLiveLessonName(lesson.getName());
        super.setLiveLessonNotes(lesson.getNotes());
    }

    @Override
    protected void saveLessonName(String newValue) {
        Lesson lesson = liveLesson.getValue();
        if(lesson == null){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_update_lesson_name));
            return;
        }

        lesson.setName(newValue);

        GuestLessonService.getInstance().update(lesson, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.succes_update_lesson_name));
            }
            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_update_lesson_name));
            }
        });
    }

    @Override
    protected void saveLessonNotes(String newValue) {
        Lesson lesson = liveLesson.getValue();
        if(lesson == null){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_update_notes));
            return;
        }

        // set new notes
        lesson.setNotes(newValue);

        // and update
        GuestLessonService.getInstance().update(lesson, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.succes_update_notes));
            }
            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_update_notes));
            }
        });
    }


}
