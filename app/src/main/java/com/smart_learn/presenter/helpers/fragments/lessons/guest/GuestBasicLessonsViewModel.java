package com.smart_learn.presenter.helpers.fragments.lessons.guest;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.adapters.lessons.GuestLessonsAdapter;
import com.smart_learn.presenter.helpers.fragments.lessons.BasicLessonsViewModel;

import org.jetbrains.annotations.NotNull;

public abstract class GuestBasicLessonsViewModel extends BasicLessonsViewModel<GuestLessonsAdapter> {

    public GuestBasicLessonsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
