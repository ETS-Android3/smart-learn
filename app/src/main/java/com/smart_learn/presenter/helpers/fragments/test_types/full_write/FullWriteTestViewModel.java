package com.smart_learn.presenter.helpers.fragments.test_types.full_write;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.entities.Question;
import com.smart_learn.data.entities.QuestionFullWrite;
import com.smart_learn.data.entities.Test;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.presenter.helpers.fragments.test_types.BasicTestTypeViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class FullWriteTestViewModel extends BasicTestTypeViewModel {

    private final MutableLiveData<String> liveUserAnswer;

    public FullWriteTestViewModel(@NonNull @NotNull Application application) {
        super(application);
        liveUserAnswer = new MutableLiveData<>("");
    }

    @Override
    protected ArrayList<Question> getAllTestQuestions(@NonNull @NotNull Test test) {
        return new ArrayList<>(QuestionFullWrite.fromJsonToList(test.getQuestionsJson()));
    }

    @Override
    protected void showCustomValues(@NonNull @NotNull Question currentQuestion, boolean isReversed) {
       // no action needed here
    }

    @Override
    protected Question getProcessedQuestion(@NonNull @NotNull Question question, boolean isReversed) {
       ((QuestionFullWrite)question).setUserAnswer(liveUserAnswer.getValue(), isReversed);
       liveUserAnswer.setValue("");
       return question;
    }

    @Override
    protected String getQuestionsJson(@NonNull @NotNull ArrayList<Question> questions) {
        ArrayList<QuestionFullWrite> tmp = new ArrayList<>();
        for(Question item : questions){
            tmp.add((QuestionFullWrite)item);
        }
        return DataUtilities.General.fromListToJson(tmp);
    }
}

