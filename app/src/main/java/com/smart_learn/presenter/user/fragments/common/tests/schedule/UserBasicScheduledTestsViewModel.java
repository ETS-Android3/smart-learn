package com.smart_learn.presenter.user.fragments.common.tests.schedule;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.user.adapters.test.UserScheduledTestsAdapter;
import com.smart_learn.presenter.common.fragments.test.tests_list.BasicTestViewModel;

import org.jetbrains.annotations.NotNull;


public abstract class UserBasicScheduledTestsViewModel extends BasicTestViewModel<UserScheduledTestsAdapter> {
    public UserBasicScheduledTestsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
