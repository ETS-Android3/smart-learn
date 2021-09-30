package com.smart_learn.presenter.activities.notebook.guest.fragments.words;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.fragments.words.guest.standard.GuestStandardWordsViewModel;

import org.jetbrains.annotations.NotNull;

public class GuestWordsViewModel extends GuestStandardWordsViewModel {
    public GuestWordsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
