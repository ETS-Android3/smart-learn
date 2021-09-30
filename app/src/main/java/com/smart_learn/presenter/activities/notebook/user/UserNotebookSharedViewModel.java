package com.smart_learn.presenter.activities.notebook.user;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.presenter.activities.notebook.helpers.NotebookSharedViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserNotebookSharedViewModel extends NotebookSharedViewModel {

    private DocumentSnapshot selectedLesson;
    private DocumentSnapshot selectedWord;
    private DocumentSnapshot selectedExpression;

    // TODO: change name 'shared lesson' because can be confused with 'share lesson' option
    // used for shared lessons (not share lesson option)
    private boolean addNewEmptySharedLesson;
    private String newEmptySharedLessonName;
    private boolean isSharedLessonSelected;
    @Nullable
    private ArrayList<String> selectedSharedLessonParticipants;

    public UserNotebookSharedViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
