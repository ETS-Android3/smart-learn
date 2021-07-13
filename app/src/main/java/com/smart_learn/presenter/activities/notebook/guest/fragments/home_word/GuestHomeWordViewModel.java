package com.smart_learn.presenter.activities.notebook.guest.fragments.home_word;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.core.services.WordService;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.home_word.HomeWordViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public class GuestHomeWordViewModel extends HomeWordViewModel {

    @Getter
    private final WordService wordsService;

    public GuestHomeWordViewModel(@NonNull @NotNull Application application) {
        super(application);
        wordsService = new WordService(application);
    }
}
