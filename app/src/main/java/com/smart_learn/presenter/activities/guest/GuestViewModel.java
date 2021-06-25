package com.smart_learn.presenter.activities.guest;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.core.services.ExpressionService;
import com.smart_learn.core.services.LessonService;
import com.smart_learn.core.services.WordService;
import com.smart_learn.presenter.helpers.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

@Getter
public class GuestViewModel extends BasicAndroidViewModel {

    private final LessonService lessonService;
    private final WordService wordService;
    private final ExpressionService expressionService;

    private final LiveData<Integer> liveNumberOfLessons;
    private final LiveData<Integer> liveNumberOfWords;
    private final LiveData<Integer> liveNumberOfExpressions;
    private final LiveData<Integer> liveNumberOfActiveTests;
    private final LiveData<Integer> liveNumberOfFinishedTests;
    private final LiveData<Float> liveSuccessRate;

    public GuestViewModel(@NonNull @NotNull Application application) {
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
        liveNumberOfActiveTests = new MutableLiveData<>(0);
        liveNumberOfFinishedTests = new MutableLiveData<>(1);
        liveSuccessRate = new MutableLiveData<>(12.345678f);
    }
}
