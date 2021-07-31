package com.smart_learn.presenter.helpers.fragments.test_finalize;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.presenter.helpers.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

import lombok.Getter;

public abstract class FinalizeTestViewModel extends BasicAndroidViewModel {

    @Getter
    @NonNull
    @NotNull
    private String testId;
    @Getter
    private int testType;

    private final MutableLiveData<String> liveCorrectAnswersDescription;
    private final MutableLiveData<String> liveSuccessRateDescription;

    public FinalizeTestViewModel(@NonNull @NotNull Application application) {
        super(application);
        testId = "";
        liveCorrectAnswersDescription = new MutableLiveData<>("");
        liveSuccessRateDescription = new MutableLiveData<>("");
    }

    public LiveData<String> getLiveCorrectAnswersDescription(){
        return liveCorrectAnswersDescription;
    }

    public LiveData<String> getLiveSuccessRateDescription(){
        return liveSuccessRateDescription;
    }

    protected void setIdAndType(String testId, int testType){
        this.testId = testId == null ? "" : testId;
        this.testType = testType;
    }

    protected void setDescriptions(int correctAnswers, int totalQuestions){
        setCorrectAnswersDescription(correctAnswers, totalQuestions);
        setSuccessRateDescription(correctAnswers, totalQuestions);
    }

    private void setCorrectAnswersDescription(int correctAnswers, int totalQuestions){
        if(correctAnswers < 0 || (correctAnswers > totalQuestions)){
            liveCorrectAnswersDescription.setValue("");
        }
        liveCorrectAnswersDescription.setValue(correctAnswers + "/" + totalQuestions);
    }

    private void setSuccessRateDescription(int correctAnswers, int totalQuestions){
        if(totalQuestions == 0 || correctAnswers < 0 || (correctAnswers > totalQuestions)){
            liveSuccessRateDescription.setValue("");
        }
        // https://stackoverflow.com/questions/153724/how-to-round-a-number-to-n-decimal-places-in-java
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        liveSuccessRateDescription.setValue(decimalFormat.format((float)correctAnswers / (float)totalQuestions) + " %");
    }
}
