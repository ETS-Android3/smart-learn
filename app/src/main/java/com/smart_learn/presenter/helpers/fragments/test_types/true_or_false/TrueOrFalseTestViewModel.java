package com.smart_learn.presenter.helpers.fragments.test_types.true_or_false;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.data.entities.Question;
import com.smart_learn.data.entities.QuestionTrueOrFalse;
import com.smart_learn.data.entities.Test;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.presenter.helpers.fragments.test_types.BasicTestTypeViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Setter;


public abstract class TrueOrFalseTestViewModel extends BasicTestTypeViewModel {

    @Setter
    private int userAnswer;

    // for views
    private final MutableLiveData<String> liveOption;

    public TrueOrFalseTestViewModel(@NonNull @NotNull Application application) {
        super(application);
        userAnswer = QuestionTrueOrFalse.NO_RESPONSE;
        liveOption = new MutableLiveData<>("");
    }

    public LiveData<String> getLiveOption(){
        return liveOption;
    }

    @Override
    protected ArrayList<Question> getAllTestQuestions(@NonNull @NotNull Test test) {
        return new ArrayList<>(QuestionTrueOrFalse.fromJsonToList(test.getQuestionsJson()));
    }

    @Override
    protected void showCustomValues(@NonNull @NotNull Question currentQuestion, boolean isReversed) {
        QuestionTrueOrFalse tmp = (QuestionTrueOrFalse) currentQuestion;
        liveOption.setValue(tmp.getOption(tmp.isReversed()));
    }

    @Override
    protected Question getProcessedQuestion(@NonNull @NotNull Question question, boolean isReversed) {
        ((QuestionTrueOrFalse)question).setUserAnswer(userAnswer, isReversed);
        userAnswer = QuestionTrueOrFalse.NO_RESPONSE;
        return question;
    }

    @Override
    protected String getQuestionsJson(@NonNull @NotNull ArrayList<Question> questions) {
        ArrayList<QuestionTrueOrFalse> tmp = new ArrayList<>();
        for(Question item : questions){
            tmp.add((QuestionTrueOrFalse)item);
        }
        return DataUtilities.General.fromListToJson(tmp);
    }
}
