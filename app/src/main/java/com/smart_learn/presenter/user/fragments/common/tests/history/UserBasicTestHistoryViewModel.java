package com.smart_learn.presenter.user.fragments.common.tests.history;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.user.adapters.test.UserTestHistoryAdapter;
import com.smart_learn.presenter.common.fragments.test.tests_list.BasicTestViewModel;

import org.jetbrains.annotations.NotNull;


public abstract class UserBasicTestHistoryViewModel extends BasicTestViewModel<UserTestHistoryAdapter> {
    public UserBasicTestHistoryViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
