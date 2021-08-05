package com.smart_learn.data.firebase.firestore.entities;

import com.smart_learn.data.firebase.firestore.entities.helpers.BasicNotebookCommonDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class LessonEntranceDocument extends BasicNotebookCommonDocument {

    public interface Fields {
        String IS_FAVOURITE_FIELD_NAME = "isFavourite";
        String LANGUAGE_FIELD_NAME = "language";
        String IS_FROM_SHARED_LESSON_FIELD_NAME = "fromSharedLesson";
        String OWNER_DISPLAY_NAME_FIELD_NAME = "ownerDisplayName";
        String TRANSLATIONS_FIELD_NAME = "translations";
    }

    private boolean isFavourite;
    private String language;
    private boolean isFromSharedLesson;
    // used for a fast access at owner display name
    private String ownerDisplayName = "";

    // array list of translations transformed in string with Gson
    protected String translations;

    public LessonEntranceDocument() {
        // needed for Firestore
    }

    public LessonEntranceDocument(DocumentMetadata documentMetadata, String notes, boolean isFavourite, String language, boolean isFromSharedLesson, String translations) {
        super(documentMetadata, notes);
        this.isFavourite = isFavourite;
        this.language = language;
        this.isFromSharedLesson = isFromSharedLesson;
        this.translations = translations;
    }

    public static HashMap<String, Object> convertDocumentToHashMap(LessonEntranceDocument document){
        if(document == null){
            return new HashMap<>();
        }

        HashMap<String, Object> data = BasicNotebookCommonDocument.convertDocumentToHashMap(document);
        data.put(Fields.IS_FAVOURITE_FIELD_NAME, document.getFavourite());
        data.put(Fields.LANGUAGE_FIELD_NAME, document.getLanguage());
        data.put(Fields.IS_FROM_SHARED_LESSON_FIELD_NAME, document.isFromSharedLesson());
        data.put(Fields.OWNER_DISPLAY_NAME_FIELD_NAME, document.getOwnerDisplayName());
        data.put(Fields.TRANSLATIONS_FIELD_NAME, document.getTranslations());

        return data;
    }

    public void setOwnerDisplayName(String ownerDisplayName) {
        this.ownerDisplayName = ownerDisplayName == null ? "" : ownerDisplayName;
    }

    /**
     * This getter must have this form in order to work with Firestore. If you leave getter generated
     * by Lombok using @Getter then isFavourite() will be generated and will not work with Firestore.
     * */
    public boolean getFavourite() {
        return isFavourite;
    }
}
