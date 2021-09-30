package com.smart_learn.presenter.helpers.fragments.tests.schedule.user;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.adapters.test.schedule.UserScheduledTestsAdapter;
import com.smart_learn.presenter.helpers.fragments.tests.BasicTestViewModel;

import org.jetbrains.annotations.NotNull;


public abstract class UserBasicScheduledTestsViewModel extends BasicTestViewModel<UserScheduledTestsAdapter> {
    public UserBasicScheduledTestsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
