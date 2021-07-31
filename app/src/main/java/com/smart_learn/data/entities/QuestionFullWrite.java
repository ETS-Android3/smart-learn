package com.smart_learn.data.entities;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.presenter.helpers.PresenterHelpers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class QuestionFullWrite extends Question implements PresenterHelpers.DiffUtilCallbackHelper<QuestionFullWrite> {

    private final String correctAnswer;
    private final String correctAnswerReversed;

    private String userAnswer;

    public QuestionFullWrite(int id, int type, String questionValue, String questionValueReversed, String correctAnswer,
                             String correctAnswerReversed) {
        super(id,type, questionValue, questionValueReversed);
        this.correctAnswer = correctAnswer == null ? "" : correctAnswer.trim();
        this.correctAnswerReversed = correctAnswerReversed == null ? "" : correctAnswerReversed.trim();
    }

    public void setUserAnswer(String userAnswer, boolean isReversed) {
        this.isReversed = isReversed;
        if(userAnswer == null){
            userAnswer = "";
        }
        this.userAnswer = userAnswer.trim();
        isAnswered = true;
        if(isReversed){
           isAnswerCorrect = this.userAnswer.toLowerCase().equals(correctAnswerReversed.toLowerCase());
        }
        else{
            isAnswerCorrect = this.userAnswer.toLowerCase().equals(correctAnswer.toLowerCase());
        }
    }

    @Override
    public boolean areItemsTheSame(QuestionFullWrite item) {
        return super.areItemsTheSame(item);
    }

    @Override
    public boolean areContentsTheSame(QuestionFullWrite newItem){
        if(newItem == null){
            return false;
        }
        return super.areContentsTheSame(newItem) &&
                CoreUtilities.General.areObjectsTheSame(this.correctAnswer, newItem.getCorrectAnswer()) &&
                CoreUtilities.General.areObjectsTheSame(this.correctAnswerReversed, newItem.getCorrectAnswerReversed()) &&
                CoreUtilities.General.areObjectsTheSame(this.userAnswer, newItem.getUserAnswer());
    }

    public static QuestionFullWrite generateEmptyObject(){
        return new QuestionFullWrite(NO_ID, Types.QUESTION_FULL_WRITE,"", "", "", "");
    }

    public static ArrayList<QuestionFullWrite> fromJsonToList(String value) {
        if (value == null) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<QuestionFullWrite>>() {}.getType();
        return gson.fromJson(value, type);
    }
}
