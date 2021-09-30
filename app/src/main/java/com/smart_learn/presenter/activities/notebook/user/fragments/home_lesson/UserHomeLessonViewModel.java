package com.smart_learn.presenter.activities.notebook.user.fragments.home_lesson;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.services.lesson.UserLessonService;
import com.smart_learn.core.services.UserService;
import com.smart_learn.data.firebase.firestore.entities.LessonDocument;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.home_lesson.HomeLessonViewModel;
import com.smart_learn.core.helpers.ApplicationController;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Getter;


public class UserHomeLessonViewModel extends HomeLessonViewModel {

    private DocumentSnapshot lessonSnapshot;
    @Getter
    private final MutableLiveData<LessonDocument> liveLesson;

    public UserHomeLessonViewModel(@NonNull @NotNull Application application) {
        super(application);
        liveLesson = new MutableLiveData<>(new LessonDocument());
    }

    public void setLiveLesson(DocumentSnapshot newSnapshot, LessonDocument newLesson){
        lessonSnapshot = newSnapshot;
        liveLesson.setValue(newLesson);
        super.setLiveLessonName(newLesson.getName());
        super.setLiveLessonNotes(newLesson.getNotes());
        super.setLiveIsOwner(newLesson.getDocumentMetadata().getOwner().equals(UserService.getInstance().getUserUid()));
    }


    @Override
    protected void saveLessonName(String newValue) {
        UserLessonService.getInstance().updateLessonName(newValue, lessonSnapshot, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.success_update_lesson_name));
            }
            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_update_lesson_name));
            }
        });
    }

    @Override
    protected void saveLessonNotes(String newValue) {
        UserLessonService.getInstance().updateLessonNotes(newValue, lessonSnapshot, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.success_update_notes));
            }
            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_update_notes));
            }
        });
    }

    @NonNull
    @NotNull
    protected ArrayList<String> getCurrentLessonParticipants(){
        LessonDocument lesson = liveLesson.getValue();
        if(lesson == null){
            return new ArrayList<>();
        }

        return lesson.getParticipants() == null ? new ArrayList<>() : lesson.getParticipants();
    }
}