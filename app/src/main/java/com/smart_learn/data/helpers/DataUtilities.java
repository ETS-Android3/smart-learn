package com.smart_learn.data.helpers;

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

    public static final class Firestore {

        /**
         * Use to check if document snapshot is not null and data exists.
         *
         * @param documentSnapshot DocumentSnapshot object to be checked.
         *
         * @return true is is valid, false otherwise.
         * */
        public static boolean isGoodDocumentSnapshot(DocumentSnapshot documentSnapshot){
            if(documentSnapshot == null){
                Timber.w("documentSnapshot is null");
                return false;
            }

            if(!documentSnapshot.exists()){
                Timber.w("document inside documentSnapshot do not exists");
                return false;
            }

            return true;
        }
    }
}
