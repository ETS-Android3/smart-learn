package com.smart_learn.data.firebase.firestore.entities.helpers;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class BasicNotebookCommonDocument {

    public interface Fields {
        String NOTES_FIELD_NAME = "notes";
    }

    private DocumentMetadata documentMetadata;
    private String notes;

    public BasicNotebookCommonDocument() {
        // needed for Firestore
    }

    public BasicNotebookCommonDocument(DocumentMetadata documentMetadata, String notes) {
        this.documentMetadata = documentMetadata;
        this.notes = notes;
    }

    public static HashMap<String, Object> convertDocumentToHashMap(BasicNotebookCommonDocument document){
        if(document == null){
            return new HashMap<>();
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put(DocumentMetadata.Fields.DOCUMENT_METADATA_FIELD_NAME, DocumentMetadata.convertDocumentToHashMap(document.getDocumentMetadata()));
        data.put(Fields.NOTES_FIELD_NAME, document.getNotes());

        return data;
    }
}
