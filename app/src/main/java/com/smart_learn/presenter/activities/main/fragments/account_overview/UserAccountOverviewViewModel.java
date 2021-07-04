package com.smart_learn.presenter.activities.main.fragments.account_overview;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.R;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.presenter.helpers.fragments.account_summary.AccountOverviewViewModel;

import org.jetbrains.annotations.NotNull;

public class UserAccountOverviewViewModel extends AccountOverviewViewModel {

    public UserAccountOverviewViewModel(@NonNull @NotNull Application application) {
        super(application);

        // TODO: Link this values in a correct way for user. Get data from user document.
        //lessonService = new LessonService(application);
        //wordService = new WordService(application);
        //expressionService = new ExpressionService(application);
        liveNumberOfLessons =  new MutableLiveData<>(1);
        liveNumberOfWords =  new MutableLiveData<>(2);
        liveNumberOfExpressions =  new MutableLiveData<>(3);
        liveNumberOfLocalActiveTests = new MutableLiveData<>(4);
        liveNumberOfLocalFinishedTests = new MutableLiveData<>(5);
        liveNumberOfOnlineActiveTests = new MutableLiveData<>(6);
        liveNumberOfOnlineFinishedTests = new MutableLiveData<>(7);
        liveSuccessRate = new MutableLiveData<>(12.345678f);

        userHelloMessage = application.getResources().getString(R.string.hi) + ", " + CoreUtilities.Auth.getUserDisplayName() + "!";
    }
}
