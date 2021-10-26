package com.smart_learn.presenter.common.fragments.expressions.helpers;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.textfield.TextInputLayout;
import com.smart_learn.R;
import com.smart_learn.data.common.helpers.DataUtilities;
import com.smart_learn.core.common.helpers.ApplicationController;
import com.smart_learn.presenter.common.view_models.BasicAndroidViewModel;

import lombok.Getter;

@Getter
public class ExpressionDialogViewModel extends BasicAndroidViewModel {

    private final MutableLiveData<String> liveExpressionValue;
    private final MutableLiveData<String> liveTranslation;

    public ExpressionDialogViewModel(@NonNull Application application) {
        super(application);
        // this should be initialized in order to avoid null on getValue for live data
        liveExpressionValue = new MutableLiveData<>("");
        liveTranslation = new MutableLiveData<>("");
    }

    @Nullable
    public String getDialogSubmittedExpressionValue(TextInputLayout textInputLayout){
        String expressionValue = liveExpressionValue.getValue();
        if(expressionValue == null || expressionValue.isEmpty()){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_required));
            return null;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(expressionValue.length() > DataUtilities.Limits.MAX_EXPRESSION){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_expression_too_long));
            return null;
        }

        textInputLayout.setError(null);
        return expressionValue;
    }

    @Nullable
    public String getDialogSubmittedTranslation(TextInputLayout textInputLayout){
        String translation = liveTranslation.getValue();
        if(translation == null || translation.isEmpty()){
            return null;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(translation.length() > DataUtilities.Limits.MAX_EXPRESSION_TRANSLATION){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_translation_too_long));
            return null;
        }

        textInputLayout.setError(null);
        return translation;
    }
}
