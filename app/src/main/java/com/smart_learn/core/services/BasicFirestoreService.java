package com.smart_learn.core.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.data.firebase.firestore.repository.BasicFirestoreRepository;
import com.smart_learn.data.helpers.DataCallbacks;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class BasicFirestoreService <T, R extends BasicFirestoreRepository<T>> {

    protected final R repository;

    public BasicFirestoreService(@NonNull @NotNull R repository) {
        this.repository = repository;
    }

    public void addDocument(@NonNull @NotNull T item, @NonNull @NotNull CollectionReference collectionReference,
                            @Nullable DataCallbacks.InsertCallback<DocumentReference> callback){
        repository.addDocument(item, collectionReference, callback);
    }

    public void updateDocument(@NonNull @NotNull Map<String,Object> updatedInfo, @NonNull @NotNull DocumentSnapshot documentSnapshot,
                               @Nullable DataCallbacks.UpdateCallback<DocumentSnapshot> callback){
        repository.updateDocument(updatedInfo, documentSnapshot, callback);
    }

    public void deleteDocument(@NonNull @NotNull DocumentSnapshot documentSnapshot,
                               @Nullable DataCallbacks.DeleteCallback<DocumentSnapshot> callback){
        repository.deleteDocument(documentSnapshot, callback);
    }

}
