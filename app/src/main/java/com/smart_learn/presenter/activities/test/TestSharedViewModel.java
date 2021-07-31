package com.smart_learn.presenter.activities.test;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smart_learn.data.entities.Test;
import com.smart_learn.presenter.helpers.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class TestSharedViewModel extends BasicAndroidViewModel {

    public static int NO_VALUE = -1;
    private int nrOfLessonWords;
    private int nrOfLessonExpressions;

    // these will retain all options for generating a new test
    @Nullable
    private Test generatedTest;

    public TestSharedViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
