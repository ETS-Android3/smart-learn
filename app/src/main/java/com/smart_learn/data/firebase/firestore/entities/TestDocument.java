package com.smart_learn.data.firebase.firestore.entities;

import com.smart_learn.data.entities.Test;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TestDocument extends Test {

    // At boolean fields if 'is' appears in front leave fields without 'is'  in
    // order to work with Firestore.
    public interface Fields {
        String IS_COUNTED_AS_FINISHED_FIELD_NAME = "countedAsFinished";
        String IS_ONLINE_FIELD_NAME = "online";
    }

    @Getter
    private DocumentMetadata documentMetadata;

    // used for local/online unscheduled tests in order to make a correct update on user counters
    private boolean isCountedAsFinished;
    // mark that test is online or local
    private boolean isOnline;

    public TestDocument() {
        // needed for Firestore
    }

    public TestDocument(DocumentMetadata documentMetadata) {
        super();
        this.documentMetadata = documentMetadata;
    }

    public static HashMap<String, Object> convertDocumentToHashMap(TestDocument testDocument){
        if(testDocument == null){
            return new HashMap<>();
        }

        HashMap<String, Object> data = Test.convertDocumentToHashMap(testDocument);
        data.put(DocumentMetadata.Fields.DOCUMENT_METADATA_FIELD_NAME, DocumentMetadata.convertDocumentToHashMap(testDocument.getDocumentMetadata()));
        data.put(Fields.IS_COUNTED_AS_FINISHED_FIELD_NAME, testDocument.isCountedAsFinished());
        data.put(Fields.IS_ONLINE_FIELD_NAME, testDocument.isOnline());

        return data;
    }

}
