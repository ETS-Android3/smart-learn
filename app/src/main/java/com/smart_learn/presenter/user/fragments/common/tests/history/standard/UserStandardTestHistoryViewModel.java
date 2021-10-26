package com.smart_learn.presenter.user.fragments.common.tests.history.standard;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.user.fragments.common.tests.history.UserBasicTestHistoryViewModel;

import org.jetbrains.annotations.NotNull;

public abstract class UserStandardTestHistoryViewModel extends UserBasicTestHistoryViewModel {

    public UserStandardTestHistoryViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}