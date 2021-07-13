package com.smart_learn.presenter.activities.notebook;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;


public abstract class NotebookSharedViewModel extends BasicAndroidViewModel {

    public NotebookSharedViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
