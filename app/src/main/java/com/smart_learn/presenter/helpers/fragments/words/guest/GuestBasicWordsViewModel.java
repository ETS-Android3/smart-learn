package com.smart_learn.presenter.helpers.fragments.words.guest;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.adapters.words.GuestWordsAdapter;
import com.smart_learn.presenter.helpers.fragments.words.BasicWordsViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

public abstract class GuestBasicWordsViewModel extends BasicWordsViewModel<GuestWordsAdapter> {

    @Getter
    @Setter
    protected int currentLessonId;

    public GuestBasicWordsViewModel(@NonNull @NotNull Application application) {
        super(application);
        currentLessonId = GuestBasicWordsFragment.NO_LESSON_SELECTED;
    }
}
