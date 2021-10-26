package com.smart_learn.presenter.guest.fragments.common.tests.history.standard;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.guest.fragments.common.tests.history.GuestBasicTestHistoryViewModel;

import org.jetbrains.annotations.NotNull;


public abstract class GuestStandardTestHistoryViewModel extends GuestBasicTestHistoryViewModel {

    public GuestStandardTestHistoryViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

}
