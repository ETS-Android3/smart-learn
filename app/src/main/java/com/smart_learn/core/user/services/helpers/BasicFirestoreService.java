package com.smart_learn.core.user.services.helpers;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.data.user.repository.helpers.BasicFirestoreRepository;
import com.smart_learn.data.common.helpers.DataCallbacks;
import com.smart_learn.data.common.helpers.DataUtilities;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import timber.log.Timber;

public abstract class BasicFirestoreService <T, R extends BasicFirestoreRepository<T>> {

    protected final R repositoryInstance;

    public BasicFirestoreService(@NonNull @NotNull R repositoryInstance) {
        this.repositoryInstance = repositoryInstance;
    }

    public void addDocument(T item, CollectionReference collectionReference, DataCallbacks.General callback){
        if(item == null){
            if(callback != null){
                callback.onFailure();
            }
            Timber.w("item is null");
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Added item " + item.toString(),
                    "NOT added item " + item.toString());
        }

        if(collectionReference == null){
            Timber.w("collectionReference is null");
            callback.onFailure();
            return;
        }

        repositoryInstance.addDocument(item, collectionReference, callback);
    }

    public void updateDocument(Map<String,Object> updatedInfo, DocumentSnapshot documentSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(documentSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Document [" + documentSnapshot.getId() + "] updated",
                    "Document [" + documentSnapshot.getId() + "] NOT updated");
        }

        if(updatedInfo == null){
            Timber.w("updatedInfo is null");
            callback.onFailure();
            return;
        }

        repositoryInstance.updateDocument(updatedInfo, documentSnapshot, callback);
    }

    public void updateDocument(Map<String,Object> updatedInfo, String documentPath, DataCallbacks.General callback){
        if(documentPath == null || documentPath.isEmpty()){
            Timber.w("documentPath is null or empty");
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Document [" + documentPath + "] updated",
                    "Document [" + documentPath + "] NOT updated");
        }

        if(updatedInfo == null){
            Timber.w("updatedInfo is null");
            callback.onFailure();
            return;
        }

        repositoryInstance.updateDocument(updatedInfo, documentPath, callback);
    }

    public void deleteDocument(DocumentSnapshot documentSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(documentSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Document [" + documentSnapshot.getId() + "] deleted",
                    "Document [" + documentSnapshot.getId() + "] NOT deleted");
        }

        repositoryInstance.deleteDocument(documentSnapshot, callback);
    }

}
