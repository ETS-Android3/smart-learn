package com.smart_learn.presenter.activities.test.guest.fragments.select_lesson;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.smart_learn.core.services.expression.GuestExpressionService;
import com.smart_learn.core.services.word.GuestWordService;
import com.smart_learn.core.services.ThreadExecutorService;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.presenter.activities.test.TestSharedViewModel;
import com.smart_learn.presenter.helpers.fragments.lessons.guest.standard.GuestStandardLessonsViewModel;

import org.jetbrains.annotations.NotNull;

public class GuestSelectLessonViewModel extends GuestStandardLessonsViewModel {

    public GuestSelectLessonViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    protected void extractLessonCounters(Lesson lesson, @NonNull @NotNull Fragment fragment,
                                         @NonNull @NotNull GuestSelectLessonViewModel.Callback callback){
        if(lesson == null){
            callback.onComplete(TestSharedViewModel.NO_VALUE, TestSharedViewModel.NO_VALUE);
            return;
        }

        ThreadExecutorService.getInstance().execute(() -> {
            int wordsNr = GuestWordService.getInstance().getNumberOfWordsForSpecificLesson(lesson.getLessonId());
            int expressionsNr = GuestExpressionService.getInstance().getNumberOfExpressionsForSpecificLesson(lesson.getLessonId());
            fragment.requireActivity().runOnUiThread(() -> callback.onComplete(wordsNr, expressionsNr));
        });
    }

    protected interface Callback{
        void onComplete(int nrOfWords, int nrOfExpressions);
    }
}
