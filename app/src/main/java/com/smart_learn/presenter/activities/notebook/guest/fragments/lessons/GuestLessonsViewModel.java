package com.smart_learn.presenter.activities.notebook.guest.fragments.lessons;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.fragments.lessons.guest.standard.GuestStandardLessonsViewModel;

import org.jetbrains.annotations.NotNull;

public class GuestLessonsViewModel extends GuestStandardLessonsViewModel {

    public GuestLessonsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

}
