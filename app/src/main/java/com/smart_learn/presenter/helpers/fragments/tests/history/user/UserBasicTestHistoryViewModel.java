package com.smart_learn.presenter.helpers.fragments.tests.history.user;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.adapters.test.history.UserTestHistoryAdapter;
import com.smart_learn.presenter.helpers.fragments.tests.BasicTestViewModel;

import org.jetbrains.annotations.NotNull;


public abstract class UserBasicTestHistoryViewModel extends BasicTestViewModel<UserTestHistoryAdapter> {
    public UserBasicTestHistoryViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
