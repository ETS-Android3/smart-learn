package com.smart_learn.presenter.helpers.fragments.account_summary;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.R;
import com.smart_learn.core.services.ExpressionService;
import com.smart_learn.core.services.LessonService;
import com.smart_learn.core.services.WordService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.presenter.helpers.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

@Getter
public class AccountOverviewViewModel extends BasicAndroidViewModel {

    // This is used in order to have same effect in fragment and view model.
    // If user is logged in, fragment will show a different test summary layout and view model
    // will set a different hello message.
    private final boolean userIsLoggedIn;

    private final LessonService lessonService;
    private final WordService wordService;
    private final ExpressionService expressionService;

    private final LiveData<Integer> liveNumberOfLessons;
    private final LiveData<Integer> liveNumberOfWords;
    private final LiveData<Integer> liveNumberOfExpressions;
    private final LiveData<Integer> liveNumberOfLocalActiveTests;
    private final LiveData<Integer> liveNumberOfLocalFinishedTests;
    private final LiveData<Integer> liveNumberOfOnlineActiveTests;
    private final LiveData<Integer> liveNumberOfOnlineFinishedTests;
    private final LiveData<Float> liveSuccessRate;

    private final String userHelloMessage;

    public AccountOverviewViewModel(@NonNull @NotNull Application application) {
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
        liveNumberOfOnlineActiveTests = new MutableLiveData<>(2);
        liveNumberOfOnlineFinishedTests = new MutableLiveData<>(3);
        liveSuccessRate = new MutableLiveData<>(12.345678f);

        userIsLoggedIn = CoreUtilities.Auth.isUserLoggedIn();
        if(userIsLoggedIn){
            userHelloMessage = application.getResources().getString(R.string.hi) + ", " + CoreUtilities.Auth.getUserDisplayName() + "!";
        }
        else{
            userHelloMessage = application.getResources().getString(R.string.hi) + "!";
        }
    }
}
