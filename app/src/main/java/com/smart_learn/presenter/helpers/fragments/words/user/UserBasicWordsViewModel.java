package com.smart_learn.presenter.helpers.fragments.words.user;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.presenter.activities.notebook.user.UserNotebookSharedViewModel;
import com.smart_learn.presenter.helpers.adapters.words.UserWordsAdapter;
import com.smart_learn.presenter.helpers.fragments.words.BasicWordsViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

public abstract class UserBasicWordsViewModel extends BasicWordsViewModel<UserWordsAdapter> {

    @Getter
    @Setter
    protected DocumentSnapshot currentLessonSnapshot;

    public UserBasicWordsViewModel(@NonNull @NotNull Application application) {
        super(application);
        currentLessonSnapshot = UserNotebookSharedViewModel.NO_DOCUMENT_SELECTED;
    }
}
