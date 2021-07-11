package com.smart_learn.data.firebase.firestore.repository;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.data.helpers.DataCallbacks;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import timber.log.Timber;

public abstract class BasicFirestoreRepository <T> {

    public static final String COLLECTION_NOTIFICATIONS = "notifications";
    public static final String COLLECTION_USERS = "users";

    public void addDocument(@NonNull @NotNull T item, @NonNull @NotNull CollectionReference collectionReference,
                            @NonNull @NotNull DataCallbacks.General callback){

        collectionReference
                .add(item)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                        if(task.isSuccessful() && task.getResult() != null){
                            callback.onSuccess();
                            return;
                        }

                        // here add operation failed
                        Timber.w(task.getException());
                        callback.onFailure();
                    }
                });
    }

    public void updateDocument(@NonNull @NotNull Map<String,Object> updatedInfo, @NonNull @NotNull DocumentSnapshot documentSnapshot,
                               @NonNull @NotNull DataCallbacks.General callback){

        documentSnapshot.getReference()
                .update(updatedInfo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            callback.onSuccess();
                            return;
                        }

                        // here update operation failed
                        Timber.w(task.getException());
                        callback.onFailure();
                    }
                });
    }

    public void deleteDocument(@NonNull @NotNull DocumentSnapshot documentSnapshot,
                               @NonNull @NotNull DataCallbacks.General callback){

        documentSnapshot.getReference()
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            callback.onSuccess();
                            return;
                        }

                        // here delete operation failed
                        Timber.w(task.getException());
                        callback.onFailure();
                    }
                });
    }

}


