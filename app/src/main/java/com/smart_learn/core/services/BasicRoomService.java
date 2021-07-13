package com.smart_learn.core.services;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smart_learn.core.utilities.CoreCallbacks;
import com.smart_learn.core.utilities.Logs;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.repository.BasicRoomRepository;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public abstract class BasicRoomService <T> {

    protected BasicRoomRepository<T> basicRoomRepository;

    @Deprecated
    // TODO: when you delete this make protected BasicRoomRepository<T> basicRoomRepository final
    public BasicRoomService() {}

    public BasicRoomService(@NonNull BasicRoomRepository<T> basicRoomRepository) {
        this.basicRoomRepository = basicRoomRepository;
    }

    @Deprecated
    public void insert(T value) {

        if (basicRoomRepository == null){
            Log.e(Logs.UNEXPECTED_ERROR,Logs.FUNCTION + "[insert in BasicRoomService] basicRoomRepository is null. " +
                    "Value was not inserted.");
            return;
        }

        basicRoomRepository.insert(value);
    }

    @Deprecated
    public void update(T value) {

        if (basicRoomRepository == null){
            Log.e(Logs.UNEXPECTED_ERROR,Logs.FUNCTION + "[update in BasicRoomService] basicRoomRepository is null. " +
                    "Value was not updated.");
            return;
        }

        basicRoomRepository.update(value);
    }

    @Deprecated
    public void delete(T value) {

        if (basicRoomRepository == null){
            Log.e(Logs.UNEXPECTED_ERROR,Logs.FUNCTION + "[delete in BasicRoomService] basicRoomRepository is null. " +
                    "Value was not deleted.");
            return;
        }

        basicRoomRepository.delete(value);
    }


    /**
     * Used to insert one value in database.
     *
     * @param value Value which will be inserted in database.
     * @param callback Callback which will manage onSuccess() action if insertion is made, or
     *                 onFailure() action if insertion failed.
     * */
    public void insert(T value, @Nullable CoreCallbacks.InsertUpdateDeleteCallback<T> callback) {
        if(value == null){
            if(callback != null){
                callback.onFailure(null);
            }
            return;
        }

        basicRoomRepository.insert(value, new DataCallbacks.InsertCallback<T>() {
            @Override
            public void onSuccess(@NonNull @NotNull T value) {
                if(callback != null){
                    callback.onSuccess(value);
                }
            }

            @Override
            public void onFailure(@Nullable T value) {
                if(callback != null){
                    callback.onFailure(value);
                }
            }
        });
    }


    /**
     * Used to update one value in database.
     *
     * @param value Value which will be updated in database.
     * @param callback Callback which will manage onSuccess() action if update is made, or
     *                 onFailure() action if update failed.
     * */
    public void update(T value, @Nullable CoreCallbacks.InsertUpdateDeleteCallback<T> callback) {
        if(value == null){
            if(callback != null){
                callback.onFailure(null);
            }
            return;
        }

        //values.setModifiedAt(System.currentTimeMillis());

        basicRoomRepository.update(value, new DataCallbacks.UpdateCallback<T>() {
            @Override
            public void onSuccess(@NonNull @NotNull T value) {
                if(callback != null){
                    callback.onSuccess(value);
                }
            }

            @Override
            public void onFailure(@NonNull @NotNull T value) {
                // reset previous update time
                //value.setModifiedAt(previousUpdateTime);
                if(callback != null){
                    callback.onFailure(value);
                }
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
    public void delete(T value, @Nullable CoreCallbacks.InsertUpdateDeleteCallback<T> callback) {
        if(value == null){
            Timber.w("value is null");
            if(callback != null){
                callback.onFailure(null);
            }
            return;
        }

        basicRoomRepository.delete(value, new DataCallbacks.DeleteCallback<T>() {
            @Override
            public void onSuccess(@Nullable T value) {
                if(callback != null){
                    callback.onSuccess(value);
                }
            }

            @Override
            public void onFailure(@NonNull @NotNull T value) {
                if(callback != null){
                    callback.onFailure(value);
                }
            }
        });
    }
}
