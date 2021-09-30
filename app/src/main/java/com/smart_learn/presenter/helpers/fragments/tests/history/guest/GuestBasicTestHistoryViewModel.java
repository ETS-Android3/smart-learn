package com.smart_learn.presenter.helpers.fragments.tests.history.guest;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.adapters.test.history.GuestTestHistoryAdapter;
import com.smart_learn.presenter.helpers.fragments.tests.BasicTestViewModel;

import org.jetbrains.annotations.NotNull;

public abstract class GuestBasicTestHistoryViewModel extends BasicTestViewModel<GuestTestHistoryAdapter> {
    public GuestBasicTestHistoryViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
