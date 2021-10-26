package com.smart_learn.presenter.guest.fragments.common.expressions.select;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.guest.fragments.common.expressions.GuestBasicExpressionsViewModel;

import org.jetbrains.annotations.NotNull;

public abstract class GuestBasicSelectExpressionsViewModel extends GuestBasicExpressionsViewModel {
    public GuestBasicSelectExpressionsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
