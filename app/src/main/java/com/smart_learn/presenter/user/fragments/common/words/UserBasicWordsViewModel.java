package com.smart_learn.presenter.user.fragments.common.words;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.presenter.user.adapters.UserWordsAdapter;
import com.smart_learn.presenter.common.fragments.words.BasicWordsViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

public abstract class UserBasicWordsViewModel extends BasicWordsViewModel<UserWordsAdapter> {

    @Getter
    @Setter
    @Nullable
    protected DocumentSnapshot currentLessonSnapshot;

    public UserBasicWordsViewModel(@NonNull @NotNull Application application) {
        super(application);
        currentLessonSnapshot = null;
    }
}
