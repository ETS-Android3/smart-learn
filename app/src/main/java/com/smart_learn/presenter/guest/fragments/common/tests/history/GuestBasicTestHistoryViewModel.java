package com.smart_learn.presenter.guest.fragments.common.tests.history;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.guest.adapters.test.GuestTestHistoryAdapter;
import com.smart_learn.presenter.common.fragments.test.tests_list.BasicTestViewModel;

import org.jetbrains.annotations.NotNull;

public abstract class GuestBasicTestHistoryViewModel extends BasicTestViewModel<GuestTestHistoryAdapter> {
    public GuestBasicTestHistoryViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
