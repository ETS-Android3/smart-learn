package com.smart_learn.presenter.activities.notebook.user.fragments;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.presenter.activities.notebook.helpers.NotebookSharedViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

public class UserNotebookSharedViewModel extends NotebookSharedViewModel {

    public static DocumentSnapshot NO_DOCUMENT_SELECTED = null;

    @Getter
    @Setter
    private DocumentSnapshot selectedLesson;
    @Getter
    @Setter
    private DocumentSnapshot selectedWord;
    @Getter
    @Setter
    private DocumentSnapshot selectedExpression;


    public UserNotebookSharedViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
