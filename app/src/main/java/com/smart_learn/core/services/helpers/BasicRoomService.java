package com.smart_learn.core.services.helpers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.helpers.DataHelpers;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.data.room.repository.BasicRoomRepository;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

/**
 * Base class for Room services operations.
 *
 * @param <T> Object type on which the service operations will be applied. Must implement
 *           DataHelpers.RoomBasicInfoHelper.
 * @param <K> Repository to handle operations, which must extend BasicRoomRepository<T,?>.
 * */
public abstract class BasicRoomService <T extends DataHelpers.RoomBasicInfoHelper, K extends BasicRoomRepository<T, ?>> {

    @NonNull
    @NotNull
    protected final K repositoryInstance;

    public BasicRoomService(@NonNull K repositoryInstance) {
        this.repositoryInstance = repositoryInstance;
    }


    /**
     * Used to insert one value in database.
     *
     * @param value Value which will be inserted in database.
     * @param callback Callback which will manage onSuccess() action if insertion is made, or
     *                 onFailure() action if insertion failed.
     * */
    public void insert(T value, @Nullable DataCallbacks.General callback) {
        if(value == null){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Value [" + value.toString() + "] inserted",
                    "Insertion for value [" + value.toString() + "] failed");
        }

        repositoryInstance.insert(value, callback);
    }


    /**
     * Used to update one value in database.
     *
     * @param value Value which will be updated in database.
     * @param callback Callback which will manage onSuccess() action if update is made, or
     *                 onFailure() action if update failed.
     * */
    public void update(T value, @Nullable DataCallbacks.General callback) {
        if(value == null){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Value [" + value.toString() + "] updated",
                    "Update for value [" + value.toString() + "] failed");
        }

        // set new update time
        long previousUpdateTime = value.getBasicInfo().getModifiedAt();
        value.getBasicInfo().setModifiedAt(System.currentTimeMillis());

        DataCallbacks.General finalCallback = callback;
        repositoryInstance.update(value, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                finalCallback.onSuccess();
            }

            @Override
            public void onFailure() {
                // reset previous update time
                value.getBasicInfo().setModifiedAt(previousUpdateTime);
                finalCallback.onFailure();
            }
        });
    }


    /**
     * Used to delete one value from database.
     *
     * @param value Value which will be deleted from database.
     * @param callback Callback which will manage onSuccess() action if deletion is made, or
     *                 onFailure() action if deletion failed.
     * */
    public void delete(T value, @Nullable DataCallbacks.General callback) {
        if(value == null){
            Timber.w("value is null");
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Value [" + value.toString() + "] deleted",
                    "Deletion for value [" + value.toString() + "] failed");
        }

        repositoryInstance.delete(value, callback);
    }
}
