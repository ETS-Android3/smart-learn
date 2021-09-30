package com.smart_learn.presenter.helpers.fragments.tests.schedule.guest.standard;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.fragments.tests.schedule.guest.GuestBasicScheduledTestsViewModel;

import org.jetbrains.annotations.NotNull;

public abstract class GuestStandardScheduledTestsViewModel extends GuestBasicScheduledTestsViewModel {

    public GuestStandardScheduledTestsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

}