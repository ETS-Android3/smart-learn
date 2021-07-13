package com.smart_learn.presenter.activities.notebook.guest.fragments.lessons;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.core.services.LessonService;
import com.smart_learn.presenter.activities.notebook.fragments.lessons.LessonsViewModel;
import com.smart_learn.presenter.activities.notebook.fragments.lessons.helpers.LessonsAdapter;

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
        //FIXME: here should be launched a dialog if lessons contains word in order to alert user
        lessonService.deleteSelectedItems();
    }
}
