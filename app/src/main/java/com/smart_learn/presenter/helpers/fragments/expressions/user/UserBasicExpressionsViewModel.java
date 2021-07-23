package com.smart_learn.presenter.helpers.fragments.expressions.user;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.presenter.helpers.adapters.expressions.UserExpressionsAdapter;
import com.smart_learn.presenter.helpers.fragments.expressions.BasicExpressionsViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

public abstract class UserBasicExpressionsViewModel extends BasicExpressionsViewModel<UserExpressionsAdapter> {

    @Getter
    @Setter
    @Nullable
    protected DocumentSnapshot currentLessonSnapshot;

    public UserBasicExpressionsViewModel(@NonNull @NotNull Application application) {
        super(application);
        currentLessonSnapshot = null;
    }
}