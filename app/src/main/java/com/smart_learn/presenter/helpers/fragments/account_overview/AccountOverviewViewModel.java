package com.smart_learn.presenter.helpers.fragments.account_overview;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.smart_learn.core.services.GuestExpressionService;
import com.smart_learn.core.services.GuestLessonService;
import com.smart_learn.core.services.GuestWordService;
import com.smart_learn.presenter.helpers.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

/**
 * From this ViewModel, UserAccountOverviewViewModel and GuestAccountOverviewViewModel will be
 * extended in order to obtain a better control flow of the navigation.
 * */
@Getter
public abstract class AccountOverviewViewModel extends BasicAndroidViewModel {

    protected LiveData<Integer> liveNumberOfLessons;
    protected LiveData<Integer> liveNumberOfWords;
    protected LiveData<Integer> liveNumberOfExpressions;
    protected LiveData<Integer> liveNumberOfLocalActiveTests;
    protected LiveData<Integer> liveNumberOfLocalFinishedTests;
    protected LiveData<Integer> liveNumberOfOnlineActiveTests;
    protected LiveData<Integer> liveNumberOfOnlineFinishedTests;
    protected LiveData<Float> liveSuccessRate;

    protected String userHelloMessage;

    public AccountOverviewViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
