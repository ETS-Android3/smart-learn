package com.smart_learn.presenter.helpers.fragments.lessons.user;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.adapters.lessons.UserLessonsAdapter;
import com.smart_learn.presenter.helpers.fragments.lessons.BasicLessonsViewModel;

import org.jetbrains.annotations.NotNull;


public abstract class UserBasicLessonsViewModel extends BasicLessonsViewModel<UserLessonsAdapter> {

    public UserBasicLessonsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}