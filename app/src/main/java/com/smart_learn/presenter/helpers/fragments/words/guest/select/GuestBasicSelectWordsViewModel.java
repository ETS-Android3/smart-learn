package com.smart_learn.presenter.helpers.fragments.words.guest.select;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.fragments.words.guest.GuestBasicWordsViewModel;

import org.jetbrains.annotations.NotNull;

public abstract class GuestBasicSelectWordsViewModel extends GuestBasicWordsViewModel {
    public GuestBasicSelectWordsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
