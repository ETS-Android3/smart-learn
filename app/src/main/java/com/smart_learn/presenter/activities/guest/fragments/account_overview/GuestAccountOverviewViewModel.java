package com.smart_learn.presenter.activities.guest.fragments.account_overview;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.R;
import com.smart_learn.core.services.ExpressionService;
import com.smart_learn.core.services.LessonService;
import com.smart_learn.core.services.WordService;
import com.smart_learn.presenter.helpers.fragments.account_overview.AccountOverviewViewModel;

import org.jetbrains.annotations.NotNull;

public class GuestAccountOverviewViewModel extends AccountOverviewViewModel {

    public GuestAccountOverviewViewModel(@NonNull @NotNull Application application) {
        super(application);

        lessonService = new LessonService(application);
        wordService = new WordService(application);
        expressionService = new ExpressionService(application);

        // Is enough to set this values once because will be linked with db using live data, so any
        // change in db will be propagated to the layout.
        liveNumberOfLessons = lessonService.getLiveNumberOfLessons();
        liveNumberOfWords = wordService.getLiveNumberOfWords();
        liveNumberOfExpressions = expressionService.getLiveNumberOfExpressions();

        // TODO: link this with correct values from db
        liveNumberOfLocalActiveTests = new MutableLiveData<>(0);
        liveNumberOfLocalFinishedTests = new MutableLiveData<>(1);
        liveSuccessRate = new MutableLiveData<>(12.345678f);

        userHelloMessage = application.getResources().getString(R.string.hi) + "!";
    }
}
