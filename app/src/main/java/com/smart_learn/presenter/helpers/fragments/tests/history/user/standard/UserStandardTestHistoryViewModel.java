package com.smart_learn.presenter.helpers.fragments.tests.history.user.standard;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.fragments.tests.history.user.UserBasicTestHistoryViewModel;

import org.jetbrains.annotations.NotNull;

public abstract class UserStandardTestHistoryViewModel extends UserBasicTestHistoryViewModel {

    public UserStandardTestHistoryViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}