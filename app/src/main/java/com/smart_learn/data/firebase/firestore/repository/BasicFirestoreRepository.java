package com.smart_learn.data.firebase.firestore.repository;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.smart_learn.data.helpers.DataCallbacks;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import timber.log.Timber;

public abstract class BasicFirestoreRepository <T> {

    public static final String COLLECTION_NOTIFICATIONS = "notifications";
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_FRIENDS = "friends";
    public static final String COLLECTION_LESSONS = "lessons";

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

    protected void commitBatch(@NonNull @NotNull WriteBatch batch, @NonNull @NotNull DataCallbacks.General callback){

        /*
          FIXME: This is a problem. 'onComplete(...) from addOnCompleteListener(...)' batch is called only
              when device is online and data was written to Firestore. If device is offline while committing
              then onComplete(...) will not be called until device come online. But data will be
              committed locally. So a device can be offline for a long period and commit lots of data.
              Using a listener while offline does not seem good. At this moment, when device is offline
              consider to commit without listeners and call directly onSuccess(...), but find a better
              solution next time.
                  OBS: A transaction works only online and will tell if is no connection, but I want
                       to make offline changes also.
                       An option can be to divide operations and to decide what operations are NOT
                       acceptable online (like adding a new friend, removing a friend, accepting a
                       friend request, sending a lesson ...) , and what operations are acceptable
                       offline (like adding a new personal lesson, updating a personal lesson,
                       deleting a personal lesson, make a local test ...).

         https://stackoverflow.com/questions/47073886/how-does-the-cloud-firestore-batched-writes-work-in-offline-mode
         https://www.py4u.net/discuss/646694
         https://firebase.google.com/docs/firestore/manage-data/transactions
         https://stackoverflow.com/questions/46672630/addoncompletelistener-not-called-offline-with-cloud-firestore
         https://stackoverflow.com/questions/53665793/firebasefirestore-different-between-oncompletelistener-onsuccesslistener-oncan



        FIXME: Also another problem is when device can not access internet. If device can not access internet
         then notConnected() will be called. But if device in the meantime can access internet
         (because ConnexionChecker verify and tell that internet is available) but application batch
         cannot detect that (I presume that this is delay) then isConnected() will be called and
         an addOnCompleteListener(..) will be attached to batch. But onComplete(...) in batch will not
         be called immediately because Firestore batch did not detect that internet is available in order
         to sync data with Firestore. So any callback which must be executed in onComplete(...)
         will be executed only after application is refreshed, so this can lead to different problems
         (like leaks in Views elements which where already closed [dialogs, fragments , ...], or other issues).

                   Here is an example of the problem code:

                     new ConnexionChecker(new ConnexionChecker.Callback() {
                        @Override
                        public void isConnected() {
                            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        callback.onSuccess();
                                        return;
                                    }
                                    Timber.w(task.getException());
                                    callback.onFailure();
                                }
                            });
                        }
                        @Override
                        public void notConnected() {
                            // some action here
                        }
                    }).check();
         */

        // Based on previous problem at this moment commit() is not checked and onSuccess() is
        // returned by default.
        batch.commit();
        callback.onSuccess();
    }
}


