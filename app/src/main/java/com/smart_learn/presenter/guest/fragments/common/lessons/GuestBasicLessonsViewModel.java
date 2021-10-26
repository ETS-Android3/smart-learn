package com.smart_learn.presenter.guest.fragments.common.lessons;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.guest.adapters.GuestLessonsAdapter;
import com.smart_learn.presenter.common.fragments.lessons.BasicLessonsViewModel;

import org.jetbrains.annotations.NotNull;

public abstract class GuestBasicLessonsViewModel extends BasicLessonsViewModel<GuestLessonsAdapter> {

    public GuestBasicLessonsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
