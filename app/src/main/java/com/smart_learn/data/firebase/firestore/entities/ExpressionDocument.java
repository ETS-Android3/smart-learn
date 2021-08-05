package com.smart_learn.data.firebase.firestore.entities;

import android.text.SpannableString;

import com.google.firebase.firestore.Exclude;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ExpressionDocument extends LessonEntranceDocument  {

    public interface Fields {
        String EXPRESSION_FIELD_NAME = "expression";
    }

    private String expression;

    @Exclude
    private SpannableString spannedExpression;

    public ExpressionDocument() {
        // needed for Firestore
    }

    public ExpressionDocument(DocumentMetadata documentMetadata, String notes, boolean isFavourite,
                              String language, boolean isFromSharedLesson, String translations, String expression) {
        super(documentMetadata, notes, isFavourite, language, isFromSharedLesson, translations);
        this.expression = expression;
    }

    @Exclude
    public SpannableString getSpannedExpression() {
        return spannedExpression;
    }

    public static HashMap<String, Object> convertDocumentToHashMap(ExpressionDocument document){
        if(document == null){
            return new HashMap<>();
        }

        HashMap<String, Object> data = LessonEntranceDocument.convertDocumentToHashMap(document);
        data.put(Fields.EXPRESSION_FIELD_NAME, document.getExpression());

        return data;
    }

    public static String convertDocumentToJson(ExpressionDocument document){
        if(document == null){
            document = new ExpressionDocument();
        }

        return new Gson().toJson(document, ExpressionDocument.class);
    }

    public static ExpressionDocument convertJsonToDocument(String json){
        if(json == null || json.isEmpty()){
            return new ExpressionDocument();
        }
        return new Gson().fromJson(json, ExpressionDocument.class);
    }

    public static String fromListToJson(ArrayList<ExpressionDocument> value) {
        if (value == null) {
            value = new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ExpressionDocument>>() {}.getType();
        return gson.toJson(value, type);
    }

    public static ArrayList<ExpressionDocument> fromJsonToList(String value) {
        if (value== null) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<ExpressionDocument>>() {}.getType();
        return gson.fromJson(value, type);
    }
}
