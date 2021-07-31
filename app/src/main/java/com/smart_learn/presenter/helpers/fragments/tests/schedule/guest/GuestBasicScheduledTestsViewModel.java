package com.smart_learn.presenter.helpers.fragments.tests.schedule.guest;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.adapters.test.schedule.GuestScheduledTestsAdapter;
import com.smart_learn.presenter.helpers.fragments.tests.BasicTestViewModel;

import org.jetbrains.annotations.NotNull;

public abstract class GuestBasicScheduledTestsViewModel extends BasicTestViewModel<GuestScheduledTestsAdapter> {
    public GuestBasicScheduledTestsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
