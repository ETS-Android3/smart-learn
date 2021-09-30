package com.smart_learn.presenter.helpers.fragments.tests.schedule.user.standard;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.fragments.tests.schedule.user.UserBasicScheduledTestsViewModel;

import org.jetbrains.annotations.NotNull;


public abstract class UserStandardScheduledTestsViewModel extends UserBasicScheduledTestsViewModel {

    public UserStandardScheduledTestsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

}