package com.smart_learn.data.common.entities.question;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smart_learn.R;
import com.smart_learn.core.common.helpers.CoreUtilities;
import com.smart_learn.core.common.helpers.ApplicationController;
import com.smart_learn.data.common.entities.question.helpers.QuestionMetadata;
import com.smart_learn.presenter.common.helpers.PresenterHelpers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class QuestionTrueOrFalse extends Question implements PresenterHelpers.DiffUtilCallbackHelper<QuestionTrueOrFalse> {

    public static final int NO_RESPONSE = 0;
    public static final int RESPONSE_TRUE = 1;
    public static final int RESPONSE_FALSE = 2;

    private final String option;
    private final String optionReversed;

    private final int correctAnswer;
    private final int correctAnswerReversed;
    private int userAnswer;

    public QuestionTrueOrFalse(long id, int type, String questionValue, String questionValueReversed, QuestionMetadata questionMetadata,
                               String option, String optionReversed, int correctAnswer, int correctAnswerReversed) {
        super(id, type, questionValue, questionValueReversed, questionMetadata);
        this.option = option == null ? "" : option;
        this.optionReversed = option == null ? "" : optionReversed;
        this.userAnswer = NO_RESPONSE;
        this.correctAnswer = correctAnswer;
        this.correctAnswerReversed = correctAnswerReversed;
    }

    public void setUserAnswer(int userAnswer, boolean isReversed) {
        this.isReversed = isReversed;
        this.userAnswer = userAnswer;
        isAnswered = true;
        if(isReversed){
            isAnswerCorrect = userAnswer == correctAnswerReversed;
        }
        else{
            isAnswerCorrect = userAnswer == correctAnswer;
        }
    }

    @Override
    public boolean areItemsTheSame(QuestionTrueOrFalse item) {
        return super.areItemsTheSame(item);
    }

    @Override
    public boolean areContentsTheSame(QuestionTrueOrFalse newItem){
        if(newItem == null){
            return false;
        }
        return super.areContentsTheSame(newItem) &&
                CoreUtilities.General.areObjectsTheSame(this.option, newItem.getOption()) &&
                CoreUtilities.General.areObjectsTheSame(this.optionReversed, newItem.getOptionReversed()) &&
                this.correctAnswer == newItem.getCorrectAnswer() &&
                this.correctAnswerReversed == newItem.getCorrectAnswerReversed() &&
                this.userAnswer == newItem.getUserAnswer();
    }

    public static QuestionTrueOrFalse generateEmptyObject(){
        return new QuestionTrueOrFalse(
                NO_ID,
                Types.QUESTION_FULL_WRITE,
                "",
                "",
                QuestionMetadata.generateEmptyObject(),
                "",
                "",
                NO_RESPONSE,
                NO_RESPONSE);
    }

    public static ArrayList<QuestionTrueOrFalse> fromJsonToList(String value) {
        if (value == null) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<QuestionTrueOrFalse>>() {}.getType();
        return gson.fromJson(value, type);
    }

    public String getOption(boolean isReversed){
        if(isReversed){
            return optionReversed;
        }
        return option;
    }

    public static String getStringAnswerValues(int option){
        switch (option){
            case RESPONSE_TRUE:
                return ApplicationController.getInstance().getString(R.string.true_option);
            case RESPONSE_FALSE:
                return ApplicationController.getInstance().getString(R.string.false_option);
            default:
                return ApplicationController.getInstance().getString(R.string.no_response_given);
        }
    }
}

