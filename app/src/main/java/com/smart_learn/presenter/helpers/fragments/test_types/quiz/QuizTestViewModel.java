package com.smart_learn.presenter.helpers.fragments.test_types.quiz;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.data.entities.Question;
import com.smart_learn.data.entities.QuestionQuiz;
import com.smart_learn.data.entities.Test;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.presenter.helpers.fragments.test_types.BasicTestTypeViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Setter;

public abstract class QuizTestViewModel extends BasicTestTypeViewModel {

    @Setter
    private int userAnswer;

    // for views
    private final MutableLiveData<String> liveOptionA;
    private final MutableLiveData<String> liveOptionB;
    private final MutableLiveData<String> liveOptionC;
    private final MutableLiveData<String> liveOptionD;

    public QuizTestViewModel(@NonNull @NotNull Application application) {
        super(application);
        userAnswer = QuestionQuiz.NO_INDEX;
        liveOptionA = new MutableLiveData<>("");
        liveOptionB = new MutableLiveData<>("");
        liveOptionC = new MutableLiveData<>("");
        liveOptionD = new MutableLiveData<>("");
    }

    public LiveData<String> getLiveOptionA(){
        return liveOptionA;
    }

    public LiveData<String> getLiveOptionB(){
        return liveOptionB;
    }

    public LiveData<String> getLiveOptionC(){
        return liveOptionC;
    }

    public LiveData<String> getLiveOptionD(){
        return liveOptionD;
    }

    @Override
    protected ArrayList<Question> getAllTestQuestions(@NonNull @NotNull Test test) {
        return new ArrayList<>(QuestionQuiz.fromJsonToList(test.getQuestionsJson()));
    }

    @Override
    protected void showCustomValues(@NonNull @NotNull Question currentQuestion, boolean isReversed) {
        QuestionQuiz tmp = (QuestionQuiz) currentQuestion;
        liveOptionA.setValue(tmp.getOptionA(tmp.isReversed()));
        liveOptionB.setValue(tmp.getOptionB(tmp.isReversed()));
        liveOptionC.setValue(tmp.getOptionC(tmp.isReversed()));
        liveOptionD.setValue(tmp.getOptionD(tmp.isReversed()));
    }



    @Override
    protected Question getProcessedQuestion(@NonNull @NotNull Question question, boolean isReversed) {
        ((QuestionQuiz)question).setUserAnswer(userAnswer, isReversed);
        userAnswer = QuestionQuiz.NO_INDEX;
        return question;
    }

    @Override
    protected String getQuestionsJson(@NonNull @NotNull ArrayList<Question> questions) {
        ArrayList<QuestionQuiz> tmp = new ArrayList<>();
        for(Question item : questions){
            tmp.add((QuestionQuiz)item);
        }
        return DataUtilities.General.fromListToJson(tmp);
    }
}
