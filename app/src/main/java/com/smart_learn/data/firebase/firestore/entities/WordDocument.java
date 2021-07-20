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

@Getter
@Setter
public class WordDocument extends LessonEntranceDocument {

    public interface Fields {
        String WORD_FIELD_NAME = "word";
        String PHONETIC_FIELD_NAME = "phonetic";
    }

    private String word;
    private String phonetic;

    @Exclude
    private SpannableString spannedWord;

    public WordDocument() {
        // needed for Firestore
    }

    public WordDocument(DocumentMetadata documentMetadata, String notes, boolean isFavourite, String language,
                        String translations, String word, String phonetic) {
        super(documentMetadata, notes, isFavourite, language, translations);
        this.word = word;
        this.phonetic = phonetic;
    }

    @Exclude
    public SpannableString getSpannedWord() {
        return spannedWord;
    }

    public static HashMap<String, Object> convertDocumentToHashMap(WordDocument document){
        if(document == null){
            return new HashMap<>();
        }

        HashMap<String, Object> data = LessonEntranceDocument.convertDocumentToHashMap(document);
        data.put(Fields.WORD_FIELD_NAME, document.getWord());
        data.put(Fields.PHONETIC_FIELD_NAME, document.getPhonetic());

        return data;
    }

    public static String convertDocumentToJson(WordDocument document){
        if(document == null){
            document = new WordDocument();
        }

        return new Gson().toJson(document, WordDocument.class);
    }

    public static WordDocument convertJsonToDocument(String json){
        if(json == null || json.isEmpty()){
            return new WordDocument();
        }
        return new Gson().fromJson(json, WordDocument.class);
    }

    public static String fromListToJson(ArrayList<WordDocument> value) {
        if (value == null) {
            value = new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<WordDocument>>() {}.getType();
        return gson.toJson(value, type);
    }

    public static ArrayList<WordDocument> fromJsonToList(String value) {
        if (value== null) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<WordDocument>>() {}.getType();
        return gson.fromJson(value, type);
    }
}
