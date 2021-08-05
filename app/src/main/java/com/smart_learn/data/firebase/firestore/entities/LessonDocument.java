package com.smart_learn.data.firebase.firestore.entities;

import android.text.SpannableString;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;
import com.google.gson.Gson;
import com.smart_learn.R;
import com.smart_learn.data.firebase.firestore.entities.helpers.BasicNotebookCommonDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.presenter.helpers.ApplicationController;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LessonDocument extends BasicNotebookCommonDocument {

    public interface Fields {
        String TYPE_FIELD_NAME = "type";
        String NAME_FIELD_NAME = "name";
        String NR_OF_WORDS_FIELD_NAME = "nrOfWords";
        String NR_OF_EXPRESSIONS_FIELD_NAME = "nrOfExpressions";
        String FROM_UID_FIELD_NAME = "fromUid";
        String FROM_DISPLAY_NAME_FIELD_NAME = "fromDisplayName";
        String FROM_DOCUMENT_REFERENCE_FIELD_NAME = "fromDocumentReference";
        String PARTICIPANTS_FIELD_NAME = "participants";
    }

    public interface Types {
        int TYPE_NONE = 0;
        int LOCAL = 1;
        int RECEIVED = 2;
        int SHARED = 3;
    }

    private int type;
    private String name;
    private int nrOfWords;
    private int nrOfExpressions;

    @Exclude
    private SpannableString spannedName;

    /* *********************************************************************************************
                           START optional fields for received and shared lessons

                        These fields are used only for received and shared lessons
    ********************************************************************************************* */
    // these will be the UID of the user who sent the lesson
    private String fromUid;
    // These will be the display name of the user who sent the lesson.
    private String fromDisplayName;
    // for fromUserReference
    private DocumentReference fromDocumentReference;
    /* *********************************************************************************************
                           END optional fields for received and shared lessons
    ********************************************************************************************* */


    /* *********************************************************************************************
                           START optional fields for shared lessons

                        These fields are used only for shared lessons
    ********************************************************************************************* */
    // used to store UID of the participants for shared lessons
    private ArrayList<String> participants = new ArrayList<>();
    /* *********************************************************************************************
                           END optional fields for shared lessons
    ********************************************************************************************* */

    public LessonDocument() {
        // needed for Firestore
    }

    public LessonDocument(DocumentMetadata documentMetadata, String notes, int type, String name) {
        super(documentMetadata, notes);
        this.type = type;
        this.name = name;
    }

    @Exclude
    public SpannableString getSpannedName() {
        return spannedName;
    }

    public static HashMap<String, Object> convertDocumentToHashMap(LessonDocument document){
        if(document == null){
            return new HashMap<>();
        }

        HashMap<String, Object> data = BasicNotebookCommonDocument.convertDocumentToHashMap(document);
        data.put(Fields.TYPE_FIELD_NAME, document.getType());
        data.put(Fields.NAME_FIELD_NAME, document.getName());
        data.put(Fields.NR_OF_WORDS_FIELD_NAME, document.getNrOfWords());
        data.put(Fields.NR_OF_EXPRESSIONS_FIELD_NAME, document.getNrOfExpressions());
        data.put(Fields.FROM_UID_FIELD_NAME, document.getFromUid());
        data.put(Fields.FROM_DISPLAY_NAME_FIELD_NAME, document.getFromDisplayName());
        data.put(Fields.FROM_DOCUMENT_REFERENCE_FIELD_NAME, document.getFromDocumentReference());
        data.put(Fields.PARTICIPANTS_FIELD_NAME, document.getParticipants());

        return data;
    }

    public static String convertDocumentToJson(LessonDocument document){
        if(document == null){
            return "{}";
        }

        return new Gson().toJson(document, LessonDocument.class);
    }

    public static LessonDocument convertJsonToDocument(String json){
        if(json == null || json.isEmpty()){
            return new LessonDocument();
        }
        return new Gson().fromJson(json, LessonDocument.class);
    }


    public static String generateLessonTypeTitle(int type){
        switch (type){
            case Types.LOCAL:
                return ApplicationController.getInstance().getString(R.string.local_lesson);
            case Types.RECEIVED:
                return ApplicationController.getInstance().getString(R.string.received_lesson);
            case Types.SHARED:
                return ApplicationController.getInstance().getString(R.string.shared_lesson);
            case Types.TYPE_NONE:
            default:
                return "";
        }
    }
}
