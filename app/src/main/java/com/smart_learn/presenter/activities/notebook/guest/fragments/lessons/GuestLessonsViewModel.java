package com.smart_learn.presenter.activities.notebook.guest.fragments.lessons;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.core.services.GuestLessonService;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.data.room.entities.helpers.BasicInfo;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.lessons.LessonsViewModel;
import com.smart_learn.presenter.activities.notebook.guest.fragments.lessons.helpers.LessonsAdapter;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

public class GuestLessonsViewModel extends LessonsViewModel<LessonsAdapter> {

    @Getter
    private final GuestLessonService guestLessonService;
    @Getter
    @Setter
    private boolean allItemsAreSelected;

    public GuestLessonsViewModel(@NonNull @NotNull Application application) {
        super(application);
        guestLessonService = new GuestLessonService(application);
        allItemsAreSelected = false;
    }

    @Override
    public void deleteSelectedItems(){
        guestLessonService.deleteSelectedItems();
    }

    @Override
    public void addLessonByName(@NonNull @NotNull String lessonName) {
        Lesson lesson = new Lesson("", false, new BasicInfo(System.currentTimeMillis()), lessonName);
        guestLessonService.tryToAddOrUpdateNewLesson(lesson, false);
    }

}
