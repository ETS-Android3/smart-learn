package com.smart_learn.presenter.activities.notebook.fragments.lessons.helpers;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.R;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.databinding.LayoutDialogAddLessonBinding;
import com.smart_learn.presenter.helpers.ApplicationController;
import com.smart_learn.presenter.helpers.BasicAndroidViewModel;

import lombok.Getter;

public class LessonDialogViewModel extends BasicAndroidViewModel {

    @Getter
    private final MutableLiveData<Lesson> liveLessonInfo;
    @Getter
    private final int MAX_LESSON_NAME;

    public LessonDialogViewModel(@NonNull Application application) {
        super(application);
        // this should be initialized in order to avoid null on getValue for live data
        // FIXME: get standard new lesson
        //liveLessonInfo = new MutableLiveData<>(new Lesson("",0,0,false));
        liveLessonInfo = new MutableLiveData<>();
        // TODO: link this with database limit
        MAX_LESSON_NAME = 50;
    }

    public Lesson getDialogSubmittedLesson(LayoutDialogAddLessonBinding dialogBinding){
        Lesson lesson = liveLessonInfo.getValue();
        if(lesson == null || TextUtils.isEmpty(lesson.getName())){
            dialogBinding.etLessonNameLayoutDialogAddLesson.setError(ApplicationController.getInstance().getString(R.string.error_required));
            return null;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(lesson.getName().length() > MAX_LESSON_NAME){
            dialogBinding.etLessonNameLayoutDialogAddLesson.setError(ApplicationController.getInstance().getString(R.string.error_lesson_name_too_long));
            return null;
        }

        dialogBinding.etLessonNameLayoutDialogAddLesson.setError(null);
        return lesson;
    }
}