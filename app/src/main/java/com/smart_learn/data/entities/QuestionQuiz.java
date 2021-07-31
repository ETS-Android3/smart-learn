package com.smart_learn.data.entities;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smart_learn.R;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.presenter.helpers.ApplicationController;
import com.smart_learn.presenter.helpers.PresenterHelpers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class QuestionQuiz extends Question implements PresenterHelpers.DiffUtilCallbackHelper<QuestionQuiz> {

    public static final int OPTIONS_NR = 4;
    public static final int NO_INDEX = -1;
    public static final int INDEX_OPTION_A = 0;
    public static final int INDEX_OPTION_B = 1;
    public static final int INDEX_OPTION_C = 2;
    public static final int INDEX_OPTION_D = 3;

    private final ArrayList<String> options;
    private final ArrayList<String> optionsReversed;

    private final int correctAnswer;
    private final int correctAnswerReversed;
    private int userAnswer;

    public QuestionQuiz(int id, int type, String questionValue, String questionValueReversed, ArrayList<String> options,
                        ArrayList<String> optionsReversed, int correctAnswer, int correctAnswerReversed) {
        super(id, type, questionValue, questionValueReversed);
        this.userAnswer = NO_INDEX;

        if(options == null){
            options = getEmptyOptionsArray();
        }
        if(options.size() != OPTIONS_NR){
            options = options.size() > OPTIONS_NR ?
                    (ArrayList<String>) options.subList(0, OPTIONS_NR) : getEmptyOptionsArray(options.size());
        }
        this.options = options;

        if(optionsReversed == null){
            optionsReversed = getEmptyOptionsArray();
        }
        if(optionsReversed.size() != OPTIONS_NR){
            optionsReversed = optionsReversed.size() > OPTIONS_NR ?
                    (ArrayList<String>) optionsReversed.subList(0, OPTIONS_NR) : getEmptyOptionsArray(optionsReversed.size());
        }
        this.optionsReversed = optionsReversed;

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
    public boolean areItemsTheSame(QuestionQuiz item) {
        return super.areItemsTheSame(item);
    }

    @Override
    public boolean areContentsTheSame(QuestionQuiz newItem){
        if(newItem == null){
            return false;
        }
        return super.areContentsTheSame(newItem) &&
                CoreUtilities.General.areObjectsTheSame(this.options, newItem.getOptions()) &&
                CoreUtilities.General.areObjectsTheSame(this.optionsReversed, newItem.getOptionsReversed()) &&
                this.correctAnswer == newItem.getCorrectAnswer() &&
                this.correctAnswerReversed == newItem.getCorrectAnswerReversed() &&
               this.userAnswer == newItem.getUserAnswer();
    }

    public static QuestionQuiz generateEmptyObject(){
        return new QuestionQuiz(NO_ID, Types.QUESTION_QUIZ, "", "",
                getEmptyOptionsArray(), getEmptyOptionsArray(), 0, 0);
    }

    private static ArrayList<String> getEmptyOptionsArray(){
        return getEmptyOptionsArray(0);
    }

    private static ArrayList<String> getEmptyOptionsArray(int startIndex){
        ArrayList<String> tmp = new ArrayList<>();
        for(int i = startIndex; i < OPTIONS_NR; i++){
            tmp.add("");
        }
        return tmp;
    }

    public static String getStringAnswerValues(int option){
        switch (option){
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
            default:
                return ApplicationController.getInstance().getString(R.string.no_response_given);
        }
    }

    public static ArrayList<QuestionQuiz> fromJsonToList(String value) {
        if (value == null) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<QuestionQuiz>>() {}.getType();
        return gson.fromJson(value, type);
    }

    public String getOptionA(boolean isReversed){
        if(isReversed){
            return optionsReversed.size() > INDEX_OPTION_A ? optionsReversed.get(INDEX_OPTION_A) : "";
        }
        return options.size() > INDEX_OPTION_A ? options.get(INDEX_OPTION_A) : "";
    }

    public String getOptionB(boolean isReversed){
        if(isReversed){
            return optionsReversed.size() > INDEX_OPTION_B ? optionsReversed.get(INDEX_OPTION_B) : "";
        }
        return options.size() > INDEX_OPTION_B ? options.get(INDEX_OPTION_B) : "";
    }

    public String getOptionC(boolean isReversed){
        if(isReversed){
            return optionsReversed.size() > INDEX_OPTION_C ? optionsReversed.get(INDEX_OPTION_C) : "";
        }
        return options.size() > INDEX_OPTION_C ? options.get(INDEX_OPTION_C) : "";
    }

    public String getOptionD(boolean isReversed){
        if(isReversed){
            return optionsReversed.size() > INDEX_OPTION_D ? optionsReversed.get(INDEX_OPTION_D) : "";
        }
        return options.size() > INDEX_OPTION_D ? options.get(INDEX_OPTION_D) : "";
    }

}
