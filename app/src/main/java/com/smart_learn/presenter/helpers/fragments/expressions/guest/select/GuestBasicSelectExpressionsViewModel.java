package com.smart_learn.presenter.helpers.fragments.expressions.guest.select;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.fragments.expressions.guest.GuestBasicExpressionsViewModel;

import org.jetbrains.annotations.NotNull;

public abstract class GuestBasicSelectExpressionsViewModel extends GuestBasicExpressionsViewModel {
    public GuestBasicSelectExpressionsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
