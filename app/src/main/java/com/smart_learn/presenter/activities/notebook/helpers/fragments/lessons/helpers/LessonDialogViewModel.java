package com.smart_learn.presenter.activities.notebook.helpers.fragments.lessons.helpers;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.R;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.databinding.LayoutDialogAddLessonBinding;
import com.smart_learn.presenter.helpers.ApplicationController;
import com.smart_learn.presenter.helpers.view_models.BasicAndroidViewModel;

import lombok.Getter;

public class LessonDialogViewModel extends BasicAndroidViewModel {

    @Getter
    private final MutableLiveData<String> liveLessonName;
    @Getter
    private final int MAX_LESSON_NAME;

    public LessonDialogViewModel(@NonNull Application application) {
        super(application);
        MAX_LESSON_NAME = DataUtilities.Limits.MAX_LESSON_NAME;
        // this should be initialized in order to avoid null on getValue for live data
        liveLessonName = new MutableLiveData<>("");
    }

    public String getDialogSubmittedLessonName(LayoutDialogAddLessonBinding dialogBinding){
        String lessonName = liveLessonName.getValue();
        if(lessonName == null || lessonName.isEmpty()){
            dialogBinding.etLessonNameLayoutDialogAddLesson.setError(ApplicationController.getInstance().getString(R.string.error_required));
            return null;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(lessonName.length() > MAX_LESSON_NAME){
            dialogBinding.etLessonNameLayoutDialogAddLesson.setError(ApplicationController.getInstance().getString(R.string.error_lesson_name_too_long));
            return null;
        }

        dialogBinding.etLessonNameLayoutDialogAddLesson.setError(null);
        return lessonName;
    }
}