package com.smart_learn.data.entities;

import android.text.TextUtils;

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

    private final ArrayList<String> correctAnswers;
    private final ArrayList<String> correctAnswersReversed;

    private String userAnswer;

    public QuestionFullWrite(long id, int type, String questionValue, String questionValueReversed, QuestionMetadata questionMetadata,
                             ArrayList<String> correctAnswers, ArrayList<String> correctAnswersReversed) {
        super(id,type, questionValue, questionValueReversed, questionMetadata);

        if(correctAnswers == null){
            this.correctAnswers = new ArrayList<>();
        }
        else{
            this.correctAnswers = new ArrayList<>();
            for(String value : correctAnswers){
                if(!TextUtils.isEmpty(value)){
                    this.correctAnswers.add(value.trim());
                }
            }
        }

        if(correctAnswersReversed == null){
            this.correctAnswersReversed = new ArrayList<>();
        }
        else{
            this.correctAnswersReversed = new ArrayList<>();
            for(String value : correctAnswersReversed){
                if(!TextUtils.isEmpty(value)){
                    this.correctAnswersReversed.add(value.trim());
                }
            }
        }
    }

    public void setUserAnswer(String userAnswer, boolean isReversed) {
        this.isReversed = isReversed;
        if(userAnswer == null){
            userAnswer = "";
        }
        this.userAnswer = userAnswer.trim();
        isAnswered = true;

        userAnswer = this.userAnswer.toLowerCase();
        isAnswerCorrect = false;
        if(isReversed){
            for(String value : correctAnswersReversed){
                if(userAnswer.equals(value.toLowerCase())){
                    isAnswerCorrect = true;
                    return;
                }
            }
        }
        else{
            for(String value : correctAnswers){
                if(userAnswer.equals(value.toLowerCase())){
                    isAnswerCorrect = true;
                    return;
                }
            }
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
                CoreUtilities.General.areObjectsTheSame(this.correctAnswers, newItem.getCorrectAnswers()) &&
                CoreUtilities.General.areObjectsTheSame(this.correctAnswersReversed, newItem.getCorrectAnswersReversed()) &&
                CoreUtilities.General.areObjectsTheSame(this.userAnswer, newItem.getUserAnswer());
    }

    public static QuestionFullWrite generateEmptyObject(){
        return new QuestionFullWrite(
                NO_ID,
                Types.QUESTION_FULL_WRITE,
                "",
                "",
                QuestionMetadata.generateEmptyObject(),
                new ArrayList<>(),
                new ArrayList<>());
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
