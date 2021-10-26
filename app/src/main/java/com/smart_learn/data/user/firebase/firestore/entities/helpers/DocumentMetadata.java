package com.smart_learn.data.user.firebase.firestore.entities.helpers;

import com.google.firebase.firestore.Exclude;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DocumentMetadata {

    public interface Fields {
        String DOCUMENT_METADATA_FIELD_NAME = "documentMetadata";
        String OWNER_FIELD_NAME = "owner";
        String CREATED_AT_FIELD_NAME = "createdAt";
        String MODIFIED_AT_FIELD_NAME = "modifiedAt";
        String SEARCH_LIST_FIELD_NAME = "searchList";
        String COUNTED_FIELD_NAME = "counted";

        // for composed fields inside hashMap
        String COMPOSED_OWNER_FIELD_NAME = DOCUMENT_METADATA_FIELD_NAME + "." + OWNER_FIELD_NAME;
        String COMPOSED_CREATED_AT_FIELD_NAME = DOCUMENT_METADATA_FIELD_NAME + "." + CREATED_AT_FIELD_NAME;
        String COMPOSED_MODIFIED_AT_FIELD_NAME = DOCUMENT_METADATA_FIELD_NAME + "." + MODIFIED_AT_FIELD_NAME;
        String COMPOSED_SEARCH_LIST_FIELD_NAME = DOCUMENT_METADATA_FIELD_NAME + "." + SEARCH_LIST_FIELD_NAME;
        String COMPOSED_COUNTED_FIELD_NAME = DOCUMENT_METADATA_FIELD_NAME + "." + COUNTED_FIELD_NAME;
    }

    // will be the user UID (UID from Firebase Authentication)
    private String owner;
    // timestamp to store when a document is created
    private long createdAt;
    // timestamp to store when a document was modified last time
    private long modifiedAt;

    // used to add all substring which belong to the fields that will be part of the search
    private ArrayList<String> searchList;

    // mark that a document was added to counter
    private boolean counted;

    // Not needed in Firestore. Used to select/deselect documents for different actions.
    @Exclude
    private boolean selected;

    public DocumentMetadata() {
        // needed for Firestore
    }

    public DocumentMetadata(String owner, long createdAt, @NotNull @NonNull ArrayList<String> searchList) {
        this.owner = owner == null ? "" : owner;
        this.createdAt = createdAt;
        this.modifiedAt = createdAt;
        this.searchList = searchList;
    }

    public DocumentMetadata(String owner, long createdAt, long modifiedAt, @NotNull @NonNull ArrayList<String> searchList) {
        this.owner = owner == null ? "" : owner;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.searchList = searchList;
    }

    /**
     * This getter must have this form in order to work with Firestore. If you leave getter generated
     * by Lombok using @Getter then isCounted() will be generated and will not work with Firestore.
     * */
    public boolean getCounted() {
        return counted;
    }

    /**
     * Field selected is NOT needed in the firestore document.
     *
     * https://stackoverflow.com/questions/49865558/firestore-where-exactly-to-put-the-exclude-annotation
     * */
    @Exclude
    public boolean getSelected() {
        return selected;
    }

    public static HashMap<String, Object> convertDocumentToHashMap(DocumentMetadata documentMetadata){
        if(documentMetadata == null){
            return new HashMap<>();
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put(Fields.OWNER_FIELD_NAME, documentMetadata.getOwner());
        data.put(Fields.CREATED_AT_FIELD_NAME, documentMetadata.getCreatedAt());
        data.put(Fields.MODIFIED_AT_FIELD_NAME, documentMetadata.getModifiedAt());
        data.put(Fields.SEARCH_LIST_FIELD_NAME, documentMetadata.getSearchList());
        data.put(Fields.COUNTED_FIELD_NAME, documentMetadata.getCounted());

        return data;
    }

}
