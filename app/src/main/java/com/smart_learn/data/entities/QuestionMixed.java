package com.smart_learn.data.entities;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smart_learn.core.helpers.CoreUtilities;
import com.smart_learn.presenter.helpers.PresenterHelpers;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
public class QuestionMixed extends Question implements PresenterHelpers.DiffUtilCallbackHelper<QuestionMixed> {

    // minimum of the words which should be extracted from an expression in order to create question
    public static final int MIN_WORDS_FOR_MIXING = 2;
    // how many words should be extracted from an expression in order to create question
    public static final int MAX_WORDS_FOR_MIXING = 10;

    private final ArrayList<String> startOrder;
    private final ArrayList<String> correctAnswerOrder;
    private ArrayList<String> userAnswerOrder;

    public QuestionMixed(long id, int type, String questionValue, QuestionMetadata questionMetadata,
                         ArrayList<String> startOrder, ArrayList<String> correctAnswerOrder) {
        // here question reversed is not activated
        super(id, type, questionValue, "", questionMetadata);

        this.startOrder = new ArrayList<>();
        if(startOrder == null){
            startOrder = new ArrayList<>();
        }
        for(String value : startOrder){
            this.startOrder.add(value.trim());
        }

        this.correctAnswerOrder = new ArrayList<>();
        if(correctAnswerOrder == null){
            correctAnswerOrder = new ArrayList<>();
        }
        for(String value : correctAnswerOrder){
            this.correctAnswerOrder.add(value.trim());
        }
    }

    public void setUserAnswer(ArrayList<String> userAnswerOrder) {
        if(userAnswerOrder == null){
            userAnswerOrder = new ArrayList<>();
        }
        this.userAnswerOrder = new ArrayList<>();
        for(String value : userAnswerOrder){
            this.userAnswerOrder.add(value.trim());
        }

        isAnswered = true;

        if(this.userAnswerOrder.size() != correctAnswerOrder.size()){
           isAnswerCorrect = false;
           return;
        }

        int lim = this.userAnswerOrder.size();
        for (int i = 0; i < lim; i++){
            if(!this.userAnswerOrder.get(i).toLowerCase().equals(correctAnswerOrder.get(i).toLowerCase())){
                isAnswerCorrect = false;
                return;
            }
        }

        isAnswerCorrect = true;
    }

    @Override
    public boolean areItemsTheSame(QuestionMixed item) {
        return super.areItemsTheSame(item);
    }

    @Override
    public boolean areContentsTheSame(QuestionMixed newItem){
        if(newItem == null){
            return false;
        }
        return super.areContentsTheSame(newItem) &&
                CoreUtilities.General.areObjectsTheSame(this.startOrder, newItem.getStartOrder()) &&
                CoreUtilities.General.areObjectsTheSame(this.correctAnswerOrder, newItem.getCorrectAnswerOrder()) &&
                CoreUtilities.General.areObjectsTheSame(this.userAnswerOrder, newItem.getCorrectAnswerOrder());
    }

    public static QuestionMixed generateEmptyObject(){
        return new QuestionMixed(
                NO_ID,
                Types.QUESTION_MIXED,
                "",
                QuestionMetadata.generateEmptyObject(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    public static ArrayList<QuestionMixed> fromJsonToList(String value) {
        if (value == null) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<QuestionMixed>>() {}.getType();
        return gson.fromJson(value, type);
    }

    @NonNull
    @NotNull
    public static String convertOrderToString(ArrayList<String> orderList, boolean areLetters){
        if(orderList == null || orderList.isEmpty()){
            return "";
        }

        StringBuilder tmp = new StringBuilder();
        for(String item : orderList){
            tmp.append(item.trim());
            // for words concatenation append a space between words
            if(!areLetters){
                tmp.append(" ");
            }
        }

        if(!areLetters){
            // return without final space if are words
            return tmp.substring(0, tmp.length() - 1);
        }
        return tmp.substring(0, tmp.length());
    }

    public static String convertOrderToString(ArrayList<String> orderList){
        if(orderList == null || orderList.isEmpty()){
            return "";
        }
        // This question type contain only mixed letters or only mixed words.
        for(String item : orderList){
            if(item.length() > 1){
                return convertOrderToString(orderList, false);
            }
        }
        return convertOrderToString(orderList, true);
    }
}

