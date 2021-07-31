package com.smart_learn.presenter.activities.test.guest;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.activities.test.TestSharedViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestTestSharedViewModel extends TestSharedViewModel {

    public static int NO_ITEM_SELECTED = -1;

    private int selectedTestHistoryId;
    private int selectedTestScheduledId;

    public GuestTestSharedViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
