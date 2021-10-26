package com.smart_learn.presenter.guest.fragments.common.words;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.guest.adapters.GuestWordsAdapter;
import com.smart_learn.presenter.common.fragments.words.BasicWordsViewModel;
import com.smart_learn.presenter.guest.fragments.common.words.GuestBasicWordsFragment;

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
