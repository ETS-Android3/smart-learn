package com.smart_learn.data.common.helpers;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

/**
 * Used to keep all callbacks for data layer.
 * */
public interface DataCallbacks {

    /**
     * Use to manage all major operations on repository.
     * */
    interface General {
        void onSuccess();
        void onFailure();
    }

    /**
     * Use to manage Room db insertions when id is needed.
     * */
    interface RoomInsertionCallback {
        void onSuccess(long id);
        void onFailure();
    }


    /**
     * Used when a user is searched.
     * */
    interface SearchUserCallback {

        /**
         * If searched user is found this will be called.
         *
         * @param userSnapshot DocumentSnapshot where searched user data is contained. This snapshot
         *                    will contain an object of type 'UserDocument.class' .
         * @param isFriend If searched user UID is in friendsUID list of the current user.
         * @param isPending If searched user UID is in pendingUID list of the current user.
         * @param isRequestReceived If searched user UID is in receivedRequestsUID list of the
         *                         current user.
         * */
        void onSuccess(@NotNull @NonNull DocumentSnapshot userSnapshot, boolean isPending,
                       boolean isFriend, boolean isRequestReceived);

        /**
         * If user is NOT found this will be called.
         *   */
        void onFailure();
    }
}
