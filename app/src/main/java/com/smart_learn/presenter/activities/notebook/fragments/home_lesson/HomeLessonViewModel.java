package com.smart_learn.presenter.activities.notebook.fragments.home_lesson;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.textfield.TextInputLayout;
import com.smart_learn.R;
import com.smart_learn.core.services.LessonService;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.models.room.entities.Lesson;
import com.smart_learn.presenter.helpers.ApplicationController;
import com.smart_learn.presenter.helpers.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public class HomeLessonViewModel extends BasicAndroidViewModel {

    @Getter
    private final int MAX_LESSON_NAME;
    @Getter
    private final LessonService lessonService;
    @Getter
    private final MutableLiveData<Lesson> liveLesson;
    @Getter
    private String previousLessonName;

    public HomeLessonViewModel(@NonNull @NotNull Application application) {
        super(application);
        lessonService = new LessonService(application);
        liveLesson = new MutableLiveData<>(new Lesson("",0,0,false));
        // TODO: link this with database limit
        MAX_LESSON_NAME = 50;
    }

    public void setLiveLesson(Lesson lesson){
            liveLesson.setValue(lesson);
    }

    public boolean updateLessonName(TextInputLayout textInputLayout) {
        Lesson lesson = liveLesson.getValue();
        if(lesson == null || TextUtils.isEmpty(lesson.getName())){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_required));
            return false;
        }

        if(lesson.getName().equals(previousLessonName)){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_lesson_name_is_same));
            return false;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(lesson.getName().length() > MAX_LESSON_NAME){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_lesson_name_too_long));
            return false;
        }

        textInputLayout.setError(null);
        GeneralUtilities.showShortToastMessage(ApplicationController.getInstance(),lesson.getName());
        return true;
    }

    public void savePreviousLessonName(){
        Lesson lesson = liveLesson.getValue();
        if(lesson == null){
            return;
        }
        previousLessonName = lesson.getName();
    }

    public void revertToPreviousLessonName(){
        Lesson lesson = liveLesson.getValue();
        if(lesson == null){
            return;
        }
        lesson.setName(previousLessonName);
        liveLesson.setValue(lesson);
    }
}
