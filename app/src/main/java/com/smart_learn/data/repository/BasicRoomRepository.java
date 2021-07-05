package com.smart_learn.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smart_learn.core.utilities.Logs;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.dao.BasicDao;
import com.smart_learn.data.room.db.AppRoomDatabase;

import timber.log.Timber;

public abstract class BasicRoomRepository <T> {

    // https://androidx.de/androidx/room/EntityInsertionAdapter.html#insertAndReturnId(T)
    // https://stackoverflow.com/questions/64498784/check-if-row-is-inserted-into-room-database
    protected final static int INSERTION_FAILED = -1;

    protected BasicDao<T> basicDao;

    @Deprecated
    // TODO: when you delete this make protected BasicDao<T> basicDao final
    public BasicRoomRepository(){}

    public BasicRoomRepository(@NonNull BasicDao<T> basicDao) {
        this.basicDao = basicDao;
    }

    @Deprecated
    public void insert(T value) {

        if (basicDao == null){
            Log.e(Logs.UNEXPECTED_ERROR,Logs.FUNCTION + "[insert in BasicRoomRepository] basicDao is null. " +
                    "Value was not inserted.");
            return;
        }

        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            basicDao.insert(value);
        });
    }

    @Deprecated
    public void update(T value) {

        if (basicDao == null){
            Log.e(Logs.UNEXPECTED_ERROR,Logs.FUNCTION + "[update in BasicRoomRepository] basicDao is null. " +
                    "Value was not updated.");
            return;
        }

        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            basicDao.update(value);
        });
    }

    @Deprecated
    public void delete(T value) {

        if (basicDao == null){
            Log.e(Logs.UNEXPECTED_ERROR,Logs.FUNCTION + "[delete in BasicRoomRepository] basicDao is null. " +
                    "Value was not deleted.");
            return;
        }

        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            basicDao.delete(value);
        });
    }


    /**
     * Used to insert one value in database.
     *
     * @param value Value which will be inserted in database.
     * @param callback Callback which will manage onSuccess() action if insertion is made, or
     *                 onFailure() action if insertion failed.
     * */
    public void insert(@NonNull T value, @Nullable DataCallbacks.InsertCallback<T> callback) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            long rowId = basicDao.insert(value);
            if(callback == null){
                return;
            }

            // https://androidx.de/androidx/room/EntityInsertionAdapter.html#insertAndReturnId(T)
            // https://stackoverflow.com/questions/64498784/check-if-row-is-inserted-into-room-database
            if(rowId == INSERTION_FAILED){
                Timber.e("%s Insertion failed ", Logs.UNEXPECTED_ERROR);
                callback.onFailure(value);
            }
            else{
                callback.onSuccess(value);
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
    public void update(@NonNull T value, @Nullable DataCallbacks.UpdateCallback<T> callback) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            int numberOfAffectedRows = basicDao.update(value);
            if(callback == null){
                return;
            }

            // https://androidx.de/androidx/room/EntityDeletionOrUpdateAdapter.html
            // https://stackoverflow.com/questions/48519896/room-update-or-insert-if-not-exist-rows-and-return-count-changed-rows
            // update is made for one item so must be 1 row affected
            if(numberOfAffectedRows != 1){
                Timber.e(Logs.UNEXPECTED_ERROR + " rows " + numberOfAffectedRows + " were updated");
                callback.onFailure(value);
            }
            else{
                callback.onSuccess(value);
            }
        });
    }


    /**
     * Used to delete a value from database.
     *
     * @param value Value which will be deleted from database.
     * @param callback Callback which will manage onSuccess() action if deletion is made, or
     *                 onFailure() action if deletion failed.
     * */
    public void delete(@NonNull T value, @Nullable DataCallbacks.DeleteCallback<T> callback) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            int numberOfAffectedRows = basicDao.delete(value);
            if(callback == null){
                return;
            }

            // https://androidx.de/androidx/room/EntityDeletionOrUpdateAdapter.html
            // https://stackoverflow.com/questions/53448287/how-to-check-whether-record-is-delete-or-not-in-room-database
            // deletion is made for one item so must be 1 row affected
            if(numberOfAffectedRows != 1){
                Timber.e(Logs.UNEXPECTED_ERROR + " rows " + numberOfAffectedRows + " were deleted");
                callback.onFailure(value);
            }
            else{
                callback.onSuccess(value);
            }
        });
    }

}
