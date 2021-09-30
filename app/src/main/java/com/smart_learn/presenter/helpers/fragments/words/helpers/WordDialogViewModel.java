package com.smart_learn.presenter.helpers.fragments.words.helpers;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.R;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.databinding.LayoutDialogAddWordBinding;
import com.smart_learn.core.helpers.ApplicationController;
import com.smart_learn.presenter.helpers.view_models.BasicAndroidViewModel;

import lombok.Getter;

@Getter
public class WordDialogViewModel extends BasicAndroidViewModel {

    private final MutableLiveData<String> liveWordValue;
    private final MutableLiveData<String> liveTranslation;

    public WordDialogViewModel(@NonNull Application application) {
        super(application);
        // this should be initialized in order to avoid null on getValue for live data
        liveWordValue = new MutableLiveData<>("");
        liveTranslation = new MutableLiveData<>("");
    }

    @Nullable
    public String getDialogSubmittedWordValue(LayoutDialogAddWordBinding dialogBinding){
        String wordValue = liveWordValue.getValue();
        if(wordValue == null || wordValue.isEmpty()){
            dialogBinding.etWordValueLayoutDialogAddWord.setError(ApplicationController.getInstance().getString(R.string.error_required));
            return null;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(wordValue.length() > DataUtilities.Limits.MAX_WORD){
            dialogBinding.etWordValueLayoutDialogAddWord.setError(ApplicationController.getInstance().getString(R.string.error_word_too_long));
            return null;
        }

        dialogBinding.etWordValueLayoutDialogAddWord.setError(null);
        return wordValue;
    }

    @Nullable
    public String getDialogSubmittedTranslation(LayoutDialogAddWordBinding dialogBinding){
        String translation = liveTranslation.getValue();
        if(translation == null || translation.isEmpty()){
            return null;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(translation.length() > DataUtilities.Limits.MAX_WORD_TRANSLATION){
            dialogBinding.etTranslationLayoutDialogAddWord.setError(ApplicationController.getInstance().getString(R.string.error_translation_too_long));
            return null;
        }

        dialogBinding.etTranslationLayoutDialogAddWord.setError(null);
        return translation;
    }
}