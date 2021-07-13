package com.smart_learn.presenter.activities.notebook.guest.fragments.lessons;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.core.services.LessonService;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.data.room.entities.helpers.BasicInfo;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.lessons.LessonsViewModel;
import com.smart_learn.presenter.activities.notebook.guest.fragments.lessons.helpers.LessonsAdapter;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

public class GuestLessonsViewModel extends LessonsViewModel<LessonsAdapter> {

    @Getter
    private final LessonService lessonService;
    @Getter
    @Setter
    private boolean allItemsAreSelected;

    public GuestLessonsViewModel(@NonNull @NotNull Application application) {
        super(application);
        lessonService = new LessonService(application);
        allItemsAreSelected = false;
    }

    @Override
    public void deleteSelectedItems(){
        lessonService.deleteSelectedItems();
    }

    @Override
    public void addLessonByName(@NonNull @NotNull String lessonName) {
        Lesson lesson = new Lesson("", false, new BasicInfo(System.currentTimeMillis()), lessonName);
        lessonService.tryToAddOrUpdateNewLesson(lesson, false);
    }

}
