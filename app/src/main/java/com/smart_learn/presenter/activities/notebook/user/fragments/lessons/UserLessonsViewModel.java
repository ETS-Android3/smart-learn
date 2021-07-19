package com.smart_learn.presenter.activities.notebook.user.fragments.lessons;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.core.services.SettingsService;
import com.smart_learn.core.services.UserLessonService;
import com.smart_learn.core.services.UserService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.firebase.firestore.entities.LessonDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.lessons.LessonsViewModel;
import com.smart_learn.presenter.activities.notebook.user.fragments.lessons.helpers.LessonsAdapter;
import com.smart_learn.presenter.helpers.ApplicationController;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserLessonsViewModel extends LessonsViewModel<LessonsAdapter> {

    public UserLessonsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    @Override
    public void addLessonByName(@NonNull @NotNull String lessonName) {
        ArrayList<String> searchValues = new ArrayList<>();
        searchValues.add(lessonName);
        LessonDocument lesson = new LessonDocument(
                new DocumentMetadata(
                        UserService.getInstance().getUserUid(),
                        System.currentTimeMillis(),
                        CoreUtilities.General.generateSearchListForFirestoreDocument(searchValues)),
                "",
                LessonDocument.Types.LOCAL,
                lessonName
        );

        UserLessonService.getInstance().addEmptyLesson(lesson, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.success_adding_lesson));
            }

            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_adding_lesson));
            }
        });
    }

    protected void setNewOptionForShowingLessons(UserLessonsFragment fragment, int option){
        SettingsService.getInstance().saveUserLessonShowOption(option);
        if(adapter != null){
            adapter.setInitialOption(fragment);
        }
    }

}
