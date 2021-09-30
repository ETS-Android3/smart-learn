package com.smart_learn.presenter.activities.test.helpers.fragments.local_test_setup;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

public abstract class LocalTestSetupViewModel extends BasicAndroidViewModel {
    public LocalTestSetupViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
