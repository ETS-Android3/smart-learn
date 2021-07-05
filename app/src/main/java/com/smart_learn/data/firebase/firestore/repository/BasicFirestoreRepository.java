package com.smart_learn.data.firebase.firestore.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
                            @Nullable DataCallbacks.InsertCallback<DocumentReference> callback){

        collectionReference
                .add(item)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                        if(task.isSuccessful() && task.getResult() != null){
                            if(callback != null){
                                callback.onSuccess(task.getResult());
                            }
                            return;
                        }

                        // here add operation failed
                        Timber.w(task.getException());
                        if(callback != null){
                            callback.onFailure(null);
                        }
                    }
                });
    }


    public void updateDocument(@NonNull @NotNull Map<String,Object> updatedInfo, @NonNull @NotNull DocumentSnapshot documentSnapshot,
                               @Nullable DataCallbacks.UpdateCallback<DocumentSnapshot> callback){

        documentSnapshot.getReference()
                .update(updatedInfo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            if(callback != null){
                                callback.onSuccess(documentSnapshot);
                            }
                            return;
                        }

                        // here update operation failed
                        Timber.w(task.getException());
                        if(callback != null){
                            callback.onFailure(documentSnapshot);
                        }
                    }
                });
    }


    public void deleteDocument(@NonNull @NotNull DocumentSnapshot documentSnapshot,
                               @Nullable DataCallbacks.DeleteCallback<DocumentSnapshot> callback){

        documentSnapshot.getReference()
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            if(callback != null){
                                callback.onSuccess(null);
                            }
                            return;
                        }

                        // here delete operation failed
                        Timber.w(task.getException());
                        if(callback != null){
                            callback.onFailure(documentSnapshot);
                        }
                    }
                });
    }

}


