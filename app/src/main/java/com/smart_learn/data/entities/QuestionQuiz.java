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

    private final ArrayList<Integer> correctAnswers;
    private final ArrayList<Integer> correctAnswersReversed;
    private int userAnswer;

    public QuestionQuiz(long id, int type, String questionValue, String questionValueReversed, QuestionMetadata questionMetadata,
                        ArrayList<String> options, ArrayList<String> optionsReversed, ArrayList<Integer> correctAnswers,
                        ArrayList<Integer> correctAnswersReversed) {
        super(id, type, questionValue, questionValueReversed, questionMetadata);
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

        this.correctAnswers = correctAnswers == null ? new ArrayList<>() : correctAnswers;
        this.correctAnswersReversed = correctAnswersReversed == null ? new ArrayList<>() : correctAnswersReversed;
    }

    public void setUserAnswer(int userAnswer, boolean isReversed) {
        this.isReversed = isReversed;
        this.userAnswer = userAnswer;
        isAnswered = true;
        isAnswerCorrect = false;
        if(isReversed){
            for(Integer item : correctAnswersReversed){
                if(userAnswer == item){
                    isAnswerCorrect = true;
                    return;
                }
            }
        }
        else{
            for(Integer item : correctAnswers){
                if(userAnswer == item){
                    isAnswerCorrect = true;
                    return;
                }
            }
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
                CoreUtilities.General.areObjectsTheSame(this.correctAnswers, newItem.getCorrectAnswers()) &&
                CoreUtilities.General.areObjectsTheSame(this.correctAnswersReversed, newItem.getCorrectAnswersReversed()) &&
               this.userAnswer == newItem.getUserAnswer();
    }

    public static QuestionQuiz generateEmptyObject(){
        return new QuestionQuiz(
                NO_ID,
                Types.QUESTION_QUIZ,
                "",
                "",
                QuestionMetadata.generateEmptyObject(),
                getEmptyOptionsArray(),
                getEmptyOptionsArray(),
                new ArrayList<>(),
                new ArrayList<>());
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

    public static String getStringAnswerValues(ArrayList<Integer> options){
        if(options == null || options.isEmpty()){
            return ApplicationController.getInstance().getString(R.string.no_response_given);
        }

        StringBuilder response = new StringBuilder();
        int lim = options.size();
        for(int i = 0; i < lim; i++){
            switch (options.get(i)){
                case 0:
                    response.append("A, ");
                    continue;
                case 1:
                    response.append("B, ");
                    continue;
                case 2:
                    response.append("C, ");
                    continue;
                case 3:
                    response.append("D, ");
            }
        }
        // return without last 2 items ', '
        return response.substring(0, response.length() - 2);
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
