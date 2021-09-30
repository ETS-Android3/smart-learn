package com.smart_learn.presenter.helpers.fragments.words.user.select;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.fragments.words.user.UserBasicWordsViewModel;

import org.jetbrains.annotations.NotNull;


public abstract class UserBasicSelectWordsViewModel extends UserBasicWordsViewModel {
    public UserBasicSelectWordsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
