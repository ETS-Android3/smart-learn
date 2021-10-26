package com.smart_learn.presenter.common.activities.notebook;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.common.fragments.helpers.WebViewFragment;
import com.smart_learn.presenter.common.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class NotebookSharedViewModel extends BasicAndroidViewModel {

    private String meaningUrl = WebViewFragment.DEFAULT_URL;
    private String examplesUrl = WebViewFragment.DEFAULT_URL;

    public NotebookSharedViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
