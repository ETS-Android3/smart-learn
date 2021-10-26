package com.smart_learn.presenter.guest.fragments.common.tests.schedule;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.guest.adapters.test.GuestScheduledTestsAdapter;
import com.smart_learn.presenter.common.fragments.test.tests_list.BasicTestViewModel;

import org.jetbrains.annotations.NotNull;

public abstract class GuestBasicScheduledTestsViewModel extends BasicTestViewModel<GuestScheduledTestsAdapter> {
    public GuestBasicScheduledTestsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
