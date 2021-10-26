package com.smart_learn.presenter.user.fragments.common.lessons.standard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.smart_learn.R;
import com.smart_learn.core.common.services.SettingsService;
import com.smart_learn.core.user.services.UserLessonService;
import com.smart_learn.core.user.services.UserService;
import com.smart_learn.core.common.helpers.CoreUtilities;
import com.smart_learn.data.user.firebase.firestore.entities.LessonDocument;
import com.smart_learn.data.user.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.common.helpers.DataCallbacks;
import com.smart_learn.core.common.helpers.ApplicationController;
import com.smart_learn.presenter.user.fragments.common.lessons.UserBasicLessonsViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public abstract class UserStandardLessonsViewModel extends UserBasicLessonsViewModel {

    public UserStandardLessonsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

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

    protected void setNewOptionForShowingLessons(Fragment fragment, int option){
        SettingsService.getInstance().saveUserLessonShowOption(option);
        if(adapter != null){
            adapter.setInitialOption(fragment);
        }
    }
}
