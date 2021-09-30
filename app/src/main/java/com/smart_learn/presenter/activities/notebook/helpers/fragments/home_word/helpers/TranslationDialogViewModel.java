package com.smart_learn.presenter.activities.notebook.helpers.fragments.home_word.helpers;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.textfield.TextInputLayout;
import com.smart_learn.R;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.core.helpers.ApplicationController;
import com.smart_learn.presenter.helpers.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;


@Getter
public class TranslationDialogViewModel extends BasicAndroidViewModel {

    @Setter
    private boolean update;
    @Setter
    private boolean forExpression;
    @Setter
    private String previousValue;
    private final MutableLiveData<String> liveValue;
    private final MutableLiveData<String> livePhonetic;

    @Setter
    @NonNull
    @NotNull
    private ArrayList<Translation> allTranslations;

    public TranslationDialogViewModel(@NonNull Application application) {
        super(application);
        this.update = false;
        this.forExpression = false;
        this.previousValue = "";
        this.allTranslations = new ArrayList<>();
        // this should be initialized in order to avoid null on getValue for live data
        liveValue = new MutableLiveData<>("");
        livePhonetic = new MutableLiveData<>("");
    }

    protected void setLiveValue(String value){
        liveValue.setValue(value);
    }

    protected void setLivePhonetic(String value){
        livePhonetic.setValue(value);
    }

    @Nullable
    public String getDialogSubmittedValue(TextInputLayout textInputLayout){
        String value = liveValue.getValue();
        if(value == null || value.isEmpty()){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_required));
            return null;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        int lim = forExpression ? DataUtilities.Limits.MAX_EXPRESSION_TRANSLATION : DataUtilities.Limits.MAX_WORD_TRANSLATION;
        if(value.length() > lim){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_translation_too_long));
            return null;
        }

        if(!update){
            // when adding new items translation values must be unique
            for(Translation item : allTranslations){
                if (item.getTranslation().equals(value)){
                    textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_translation_exist));
                    return null;
                }
            }
        }
        else{
            // ignore if is update and value is unchanged
            if(!value.equals(previousValue)){
                // in order to be accepted new translation value must be unique
                for(Translation item : allTranslations){
                    if (item.getTranslation().equals(value)){
                        textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_translation_exist));
                        return null;
                    }
                }
            }
        }

        textInputLayout.setError(null);
        return value;
    }

    @Nullable
    public String getDialogSubmittedPhonetic(TextInputLayout textInputLayout){
        String phonetic = livePhonetic.getValue();
        if(phonetic == null){
            phonetic = "";
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        // Phonetic is used only for words.
        if(phonetic.length() > DataUtilities.Limits.MAX_WORD_PHONETIC){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_phonetic_too_long));
            return null;
        }

        textInputLayout.setError(null);
        return phonetic;
    }
}