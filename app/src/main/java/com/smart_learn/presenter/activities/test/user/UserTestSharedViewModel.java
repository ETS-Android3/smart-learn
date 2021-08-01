package com.smart_learn.presenter.activities.test.user;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.activities.test.TestSharedViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserTestSharedViewModel extends TestSharedViewModel {

    private String selectedTestHistoryId;
    private String selectedTestScheduledId;

    public UserTestSharedViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}