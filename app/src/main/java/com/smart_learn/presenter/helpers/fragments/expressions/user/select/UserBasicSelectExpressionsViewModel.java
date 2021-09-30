package com.smart_learn.presenter.helpers.fragments.expressions.user.select;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.fragments.expressions.user.UserBasicExpressionsViewModel;

import org.jetbrains.annotations.NotNull;


public abstract class UserBasicSelectExpressionsViewModel extends UserBasicExpressionsViewModel {
    public UserBasicSelectExpressionsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}