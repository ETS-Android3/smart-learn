package com.smart_learn.presenter.user.fragments.common.lessons;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.user.adapters.lessons.UserLessonsAdapter;
import com.smart_learn.presenter.common.fragments.lessons.BasicLessonsViewModel;

import org.jetbrains.annotations.NotNull;


public abstract class UserBasicLessonsViewModel extends BasicLessonsViewModel<UserLessonsAdapter> {

    public UserBasicLessonsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}