package com.smart_learn.presenter.activities.notebook.helpers.fragments.home_lesson;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.textfield.TextInputLayout;
import com.smart_learn.R;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.presenter.helpers.ApplicationController;
import com.smart_learn.presenter.helpers.dialogs.MultiLineEditableLayoutDialog;
import com.smart_learn.presenter.helpers.dialogs.SingleLineEditableLayoutDialog;
import com.smart_learn.presenter.helpers.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public abstract class HomeLessonViewModel extends BasicAndroidViewModel {

    @Getter
    private final int MAX_LESSON_NAME;
    @Getter
    private final int MAX_NOTES;

    private final MutableLiveData<String> liveLessonName;
    private final MutableLiveData<String> liveLessonNotes;
    private final MutableLiveData<String> liveExtraInfo;
    private final MutableLiveData<Integer> liveNumberOfWords;
    private final MutableLiveData<Integer> liveNumberOfExpressions;

    protected abstract void saveLessonName(String newValue);
    protected abstract void saveLessonNotes(String newValue);

    public HomeLessonViewModel(@NonNull @NotNull Application application) {
        super(application);
        MAX_LESSON_NAME = DataUtilities.Limits.MAX_LESSON_NAME;
        MAX_NOTES = DataUtilities.Limits.MAX_NOTES;
        liveLessonName = new MutableLiveData<>("");
        liveLessonNotes = new MutableLiveData<>("");
        liveExtraInfo = new MutableLiveData<>("");
        liveNumberOfWords = new MutableLiveData<>(0);
        liveNumberOfExpressions = new MutableLiveData<>(0);
    }

    public LiveData<String> getLiveLessonName() {
        return liveLessonName;
    }

    public void setLiveLessonName(String value) {
        liveLessonName.setValue(value);
    }

    public LiveData<String> getLiveLessonNotes() {
        return liveLessonNotes;
    }

    public void setLiveLessonNotes(String value) {
        liveLessonNotes.setValue(value);
    }

    public LiveData<String> getLiveExtraInfo() {
        return liveExtraInfo;
    }

    public void setLiveExtraInfo(String value) {
        liveExtraInfo.setValue(value);
    }

    public LiveData<Integer> getLiveNumberOfWords() {
        return liveNumberOfWords;
    }

    public void setLiveNumberOfWords(Integer value) {
        liveNumberOfWords.setValue(value);
    }

    public LiveData<Integer> getLiveNumberOfExpressions() {
        return liveNumberOfExpressions;
    }

    public void setLiveNumberOfExpressions(Integer value) {
        liveNumberOfExpressions.setValue(value);
    }

    protected void updateLessonName(String oldValue, String newValue, @NonNull @NotNull TextInputLayout textInputLayout,
                                       @NonNull @NotNull SingleLineEditableLayoutDialog.Listener listener) {
        if(newValue == null || newValue.isEmpty()){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_required));
            return;
        }

        if(newValue.equals(oldValue)){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_lesson_name_is_same));
            return;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(newValue.length() > MAX_LESSON_NAME){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_lesson_name_too_long));
            return;
        }

        textInputLayout.setError(null);
        listener.onSuccessCheck();
        saveLessonName(newValue);
    }

    protected void updateLessonNotes(String oldValue, String newValue, @NonNull @NotNull TextInputLayout textInputLayout,
                                        @NonNull @NotNull MultiLineEditableLayoutDialog.Listener listener) {
        if(newValue == null){
            textInputLayout.setError(null);
            listener.onSuccessCheck();
            saveLessonNotes("");
            return;
        }

        if(newValue.equals(oldValue)){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_notes_is_same));
            return;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(newValue.length() > MAX_NOTES){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_notes_too_long));
            return;
        }

        textInputLayout.setError(null);
        listener.onSuccessCheck();
        saveLessonNotes(newValue);
    }

}
