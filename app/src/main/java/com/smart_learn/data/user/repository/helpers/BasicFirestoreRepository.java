package com.smart_learn.data.user.repository.helpers;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.smart_learn.data.common.helpers.DataCallbacks;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class BasicFirestoreRepository <T> {

    public static final String COLLECTION_NOTIFICATIONS = "notifications";
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_FRIENDS = "friends";
    public static final String COLLECTION_LESSONS = "lessons";
    public static final String COLLECTION_SHARED_LESSONS = "shared_lessons";
    public static final String COLLECTION_WORDS = "words";
    public static final String COLLECTION_EXPRESSIONS = "expressions";
    public static final String COLLECTION_LOCAL_TESTS = "local_tests";
    public static final String COLLECTION_ONLINE_TESTS = "online_tests";
    public static final String COLLECTION_ONLINE_TEST_CHAT_MESSAGES = "messages";
    public static final String COLLECTION_ONLINE_TEST_PARTICIPANTS = "participants";
    public static final String FOLDER_PROFILE_PHOTOS = "profile_photos";

    public void addDocument(@NonNull @NotNull T item, @NonNull @NotNull CollectionReference collectionReference,
                            @NonNull @NotNull DataCallbacks.General callback){

        // FIXME: same problem as in commitBatch method.
        /*
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
        */
        collectionReference
                .add(item);
        callback.onSuccess();
    }

    public void updateDocument(@NonNull @NotNull Map<String,Object> updatedInfo, @NonNull @NotNull DocumentSnapshot documentSnapshot,
                               @NonNull @NotNull DataCallbacks.General callback){

        // FIXME: same problem as in commitBatch method.
        /*
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
         */

        documentSnapshot.getReference()
                .update(updatedInfo);
        callback.onSuccess();
    }

    public void updateDocument(@NonNull @NotNull Map<String,Object> updatedInfo, @NonNull @NotNull String documentPath,
                               @NonNull @NotNull DataCallbacks.General callback){

        // FIXME: same problem as in commitBatch method.
        /*


       FirebaseFirestore.getInstance()
                .document(documentPath)
                .update(updatedInfo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                            callback.onSuccess();
                            return;
                        }

                        // here update operation failed
                        Timber.w(task.getException());
                        callback.onFailure();
                        }
                });
         */

        FirebaseFirestore.getInstance().document(documentPath).update(updatedInfo);
        callback.onSuccess();
    }

    public void deleteDocument(@NonNull @NotNull DocumentSnapshot documentSnapshot,
                               @NonNull @NotNull DataCallbacks.General callback){

        // FIXME: same problem as in commitBatch method.
        /*
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
         */

        documentSnapshot.getReference()
                .delete();
        callback.onSuccess();
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


