package com.smart_learn.presenter.helpers.fragments.expressions.guest;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.adapters.expressions.GuestExpressionsAdapter;
import com.smart_learn.presenter.helpers.fragments.expressions.BasicExpressionsViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;


public abstract class GuestBasicExpressionsViewModel extends BasicExpressionsViewModel<GuestExpressionsAdapter> {

    @Getter
    @Setter
    protected int currentLessonId;

    public GuestBasicExpressionsViewModel(@NonNull @NotNull Application application) {
        super(application);
        currentLessonId = GuestBasicExpressionsFragment.NO_LESSON_SELECTED;
    }
}