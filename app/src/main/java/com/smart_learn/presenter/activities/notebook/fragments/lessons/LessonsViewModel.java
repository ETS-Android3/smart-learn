package com.smart_learn.presenter.activities.notebook.fragments.lessons;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smart_learn.core.services.LessonService;
import com.smart_learn.presenter.activities.notebook.fragments.lessons.helpers.LessonsAdapter;
import com.smart_learn.presenter.helpers.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;


public class LessonsViewModel extends BasicAndroidViewModel {

    @Getter
    private final LessonService lessonService;
    @Getter
    @Setter
    @Nullable
    private LessonsAdapter lessonsAdapter;
    @Getter
    @Setter
    private boolean allItemsAreSelected;

    public LessonsViewModel(@NonNull @NotNull Application application) {
        super(application);
        lessonService = new LessonService(application);
        allItemsAreSelected = false;
    }
    
    public void deleteSelectedItems(){
        //FIXME: here should be launched a dialog if lessons contains word in order to alert user
        lessonService.deleteSelectedItems();
    }
}
