package com.smart_learn.presenter.activities.notebook.helpers.fragments.home_expression;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.textfield.TextInputLayout;
import com.smart_learn.R;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.home_word.helpers.TranslationsAdapter;
import com.smart_learn.presenter.helpers.ApplicationController;
import com.smart_learn.presenter.helpers.dialogs.MultiLineEditableLayoutDialog;
import com.smart_learn.presenter.helpers.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;


public abstract class HomeExpressionViewModel extends BasicAndroidViewModel {

    @Nullable
    @Getter
    @Setter
    protected TranslationsAdapter adapter;
    @Getter
    protected ArrayList<Translation> allTranslations;

    protected final MutableLiveData<String> liveExpressionValue;
    protected final MutableLiveData<String> liveExpressionNotes;
    protected final MutableLiveData<Boolean> liveIsOwner;
    protected final MutableLiveData<Boolean> liveIsFromSharedLesson;
    protected final MutableLiveData<String> liveCreatedBy;

    protected abstract void saveExpressionValue(String newValue);
    protected abstract void saveExpressionNotes(String newValue);
    protected abstract void addExpressionTranslation(Translation translation);
    protected abstract void updateExpressionTranslations(ArrayList<Translation> newList, @NonNull @NotNull DataCallbacks.General callback);

    public HomeExpressionViewModel(@NonNull @NotNull Application application) {
        super(application);
        allTranslations = new ArrayList<>();
        liveExpressionValue = new MutableLiveData<>("");
        liveExpressionNotes = new MutableLiveData<>("");
        liveIsOwner = new MutableLiveData<>(false);
        liveIsFromSharedLesson = new MutableLiveData<>(false);
        liveCreatedBy = new MutableLiveData<>("");
    }

    public LiveData<String> getLiveExpressionValue() {
        return liveExpressionValue;
    }

    public void setLiveExpressionValue(String value) {
        liveExpressionValue.setValue(value);
    }

    public LiveData<String> getLiveExpressionNotes() {
        return liveExpressionNotes;
    }

    public void setLiveExpressionNotes(String value) {
        liveExpressionNotes.setValue(value);
    }

    public LiveData<Boolean> getLiveIsOwner() {
        return liveIsOwner;
    }

    public void setLiveIsOwner(boolean value) {
        liveIsOwner.setValue(value);
    }

    public LiveData<Boolean> getLiveIsFromSharedLesson() {
        return liveIsFromSharedLesson;
    }

    public void setLiveIsFromSharedLesson(boolean value) {
        liveIsFromSharedLesson.setValue(value);
    }

    public LiveData<String> getLiveCreatedBy() {
        return liveCreatedBy;
    }

    public void setLiveCreatedBy(String value) {
        liveCreatedBy.setValue(value);
    }

    protected void updateExpressionValue(String oldValue, String newValue, @NonNull @NotNull TextInputLayout textInputLayout,
                                   @NonNull @NotNull MultiLineEditableLayoutDialog.Listener listener) {
        if(newValue == null || newValue.isEmpty()){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_required));
            return;
        }

        if(newValue.equals(oldValue)){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_expression_is_same));
            return;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(newValue.length() > DataUtilities.Limits.MAX_EXPRESSION){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_expression_too_long));
            return;
        }

        textInputLayout.setError(null);
        listener.onSuccessCheck();
        saveExpressionValue(newValue);
    }

    protected void updateExpressionNotes(String oldValue, String newValue, @NonNull @NotNull TextInputLayout textInputLayout,
                                   @NonNull @NotNull MultiLineEditableLayoutDialog.Listener listener) {
        if(newValue == null){
            textInputLayout.setError(null);
            listener.onSuccessCheck();
            saveExpressionNotes("");
            return;
        }

        if(newValue.equals(oldValue)){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_notes_is_same));
            return;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(newValue.length() > DataUtilities.Limits.MAX_NOTES){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_notes_too_long));
            return;
        }

        textInputLayout.setError(null);
        listener.onSuccessCheck();
        saveExpressionNotes(newValue);
    }

    protected void addTranslation(Translation translation){
        if(translation == null){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_add_translation));
            return;
        }

        addExpressionTranslation(translation);
    }

    protected void updateTranslation(@NonNull @NotNull Translation oldTranslation, @NonNull @NotNull Translation newTranslation,
                                     @NonNull @NotNull DataCallbacks.General callback){
        int lim = allTranslations.size();
        for(int i = 0; i < lim; i++){
            // A translationValue is unique, so if are equals then we find which translation must
            // be updated.
            if(allTranslations.get(i).getTranslation().equals(oldTranslation.getTranslation())){
                // update with new translation values
                allTranslations.get(i).setTranslation(newTranslation.getTranslation());
                allTranslations.get(i).setPhonetic(newTranslation.getPhonetic());
                allTranslations.get(i).setLanguage(newTranslation.getLanguage());
                break;
            }
        }

        updateExpressionTranslations(allTranslations, callback);
    }

    protected void deleteTranslation(@NonNull @NotNull Translation translation, @NonNull @NotNull DataCallbacks.General callback){
        int lim = allTranslations.size();
        for(int i = 0; i < lim; i++){
            // A translationValue is unique, so if are equals then we find which translation must
            // be removed.
            if(allTranslations.get(i).getTranslation().equals(translation.getTranslation())){
                allTranslations.remove(i);
                break;
            }
        }

        updateExpressionTranslations(allTranslations, callback);
    }

}