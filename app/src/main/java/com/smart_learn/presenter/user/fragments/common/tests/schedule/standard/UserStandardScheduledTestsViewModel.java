package com.smart_learn.presenter.user.fragments.common.tests.schedule.standard;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.user.fragments.common.tests.schedule.UserBasicScheduledTestsViewModel;

import org.jetbrains.annotations.NotNull;


public abstract class UserStandardScheduledTestsViewModel extends UserBasicScheduledTestsViewModel {

    public UserStandardScheduledTestsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

}