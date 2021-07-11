package com.smart_learn.data.helpers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import timber.log.Timber;

/**
 * Main utilities class for data layer.
 *
 * https://projectlombok.org/features/experimental/UtilityClass
 * https://stackoverflow.com/questions/25223553/how-can-i-create-an-utility-class
 * */
public final class DataUtilities {

    /** Use a private constructor in order to avoid instantiation. */
    private DataUtilities(){
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * General utilities related to data layer.
     * */
    public static final class General {

        /**
         * Use this in order to generate a simple 'DataCallbacks.General' callback with a success
         * message and an error message. Both messages will be printed using Timber.
         *
         * @param success Success message to be printed using Timber.i
         * @param error Error message to be printed using Timber.e
         *
         * @return The callback object created.
         * */
        public static DataCallbacks.General generateGeneralCallback(String success, String error){
            return new DataCallbacks.General() {
                @Override
                public void onSuccess() {
                    Timber.i("Success: %s", success);
                }

                @Override
                public void onFailure() {
                    Timber.e("Error: %s", error);
                }
            };
        }
    }

    /**
     * Utilities related to the FirebaseFirestore.
     * */
    public static final class Firestore {

        /**
         * Use to check if document snapshot is not null and data exists.
         *
         * @param documentSnapshot DocumentSnapshot object to be checked.
         *
         * @return true if is valid, false otherwise.
         * */
        public static boolean notGoodDocumentSnapshot(DocumentSnapshot documentSnapshot){
            if(documentSnapshot == null){
                Timber.w("documentSnapshot is null");
                return true;
            }

            if(!documentSnapshot.exists()){
                Timber.w("document inside documentSnapshot do not exists");
                return true;
            }

            return false;
        }


        /**
         * Use to check if task is successful and if result is not null.
         *
         * @param task Task object to be checked.
         *
         * @return true if is NOT good configuration, false otherwise.
         * */
        public static boolean notGoodBasicResultConfiguration(Task<?> task){
            if(!task.isSuccessful()){
                Timber.w(task.getException());
                return true;
            }

            if(task.getResult() == null){
                Timber.w("task.getResult() is null");
                return true;
            }
            return false;
        }



    }
}
