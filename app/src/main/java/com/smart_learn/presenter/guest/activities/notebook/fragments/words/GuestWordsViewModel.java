package com.smart_learn.presenter.guest.activities.notebook.fragments.words;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.guest.fragments.common.words.standard.GuestStandardWordsViewModel;

import org.jetbrains.annotations.NotNull;

public class GuestWordsViewModel extends GuestStandardWordsViewModel {
    public GuestWordsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
