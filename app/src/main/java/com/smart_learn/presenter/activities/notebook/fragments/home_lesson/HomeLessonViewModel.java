package com.smart_learn.presenter.activities.notebook.fragments.home_lesson;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.textfield.TextInputLayout;
import com.smart_learn.R;
import com.smart_learn.core.services.LessonService;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.presenter.helpers.ApplicationController;
import com.smart_learn.presenter.helpers.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public abstract class HomeLessonViewModel extends BasicAndroidViewModel {

    @Getter
    protected final int MAX_LESSON_NAME;
    @Getter
    protected String previousLessonName;
    @Getter
    protected final MutableLiveData<Lesson> liveLesson;

    public HomeLessonViewModel(@NonNull @NotNull Application application) {
        super(application);
        // TODO: link this with database limit
        MAX_LESSON_NAME = 50;
        // FIXME: get standard new lesson
        //liveLesson = new MutableLiveData<>(new Lesson("",0,0,false));
        liveLesson = new MutableLiveData<>();
    }

}
