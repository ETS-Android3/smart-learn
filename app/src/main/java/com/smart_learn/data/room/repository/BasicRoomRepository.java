package com.smart_learn.data.room.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smart_learn.core.utilities.Logs;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.dao.BasicDao;
import com.smart_learn.data.room.db.AppRoomDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import timber.log.Timber;

/**
 * Base class for Room repository operations.
 *
 * @param <T> Object type on which the repository operations will be applied.
 * @param <K> DAO to handle operations, which must extend BasicDao<T>.
 * */
public abstract class BasicRoomRepository <T, K extends BasicDao<T>> {

    // https://androidx.de/androidx/room/EntityInsertionAdapter.html#insertAndReturnId(T)
    // https://stackoverflow.com/questions/64498784/check-if-row-is-inserted-into-room-database
    protected final static int INSERTION_FAILED = -1;

    @NonNull
    @NotNull
    protected final K dao;

    public BasicRoomRepository(@NonNull K dao) {
        this.dao = dao;
    }


    /**
     * Used to insert one value in database.
     *
     * @param value Value which will be inserted in database.
     * @param callback Callback which will manage onSuccess() action if insertion is made, or
     *                 onFailure() action if insertion failed.
     * */
    public void insert(@NonNull T value, @Nullable DataCallbacks.General callback) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            long rowId = dao.insert(value);
            if(callback == null){
                return;
            }

            // https://androidx.de/androidx/room/EntityInsertionAdapter.html#insertAndReturnId(T)
            // https://stackoverflow.com/questions/64498784/check-if-row-is-inserted-into-room-database
            if(rowId == INSERTION_FAILED){
                Timber.e("%s Insertion failed ", Logs.UNEXPECTED_ERROR);
                callback.onFailure();
            }
            else{
                callback.onSuccess();
            }
        });
    }

    public void insert(@NonNull T value, @Nullable DataCallbacks.RoomInsertionCallback callback) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            long rowId = dao.insert(value);
            if(callback == null){
                return;
            }

            // https://androidx.de/androidx/room/EntityInsertionAdapter.html#insertAndReturnId(T)
            // https://stackoverflow.com/questions/64498784/check-if-row-is-inserted-into-room-database
            if(rowId == INSERTION_FAILED){
                Timber.e("%s Insertion failed ", Logs.UNEXPECTED_ERROR);
                callback.onFailure();
            }
            else{
                callback.onSuccess(rowId);
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
    public void update(@NonNull T value, @Nullable DataCallbacks.General callback) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            int numberOfAffectedRows = dao.update(value);
            if(callback == null){
                return;
            }

            // https://androidx.de/androidx/room/EntityDeletionOrUpdateAdapter.html
            // https://stackoverflow.com/questions/48519896/room-update-or-insert-if-not-exist-rows-and-return-count-changed-rows
            // update is made for one item so must be 1 row affected
            if(numberOfAffectedRows != 1){
                Timber.e(Logs.UNEXPECTED_ERROR + " rows " + numberOfAffectedRows + " were updated");
                callback.onFailure();
            }
            else{
                callback.onSuccess();
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
    public void delete(@NonNull T value, @Nullable DataCallbacks.General callback) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            int numberOfAffectedRows = dao.delete(value);
            if(callback == null){
                return;
            }

            // https://androidx.de/androidx/room/EntityDeletionOrUpdateAdapter.html
            // https://stackoverflow.com/questions/53448287/how-to-check-whether-record-is-delete-or-not-in-room-database
            // deletion is made for one item so must be 1 row affected
            if(numberOfAffectedRows != 1){
                Timber.e(Logs.UNEXPECTED_ERROR + " rows " + numberOfAffectedRows + " were deleted");
                callback.onFailure();
            }
            else{
                callback.onSuccess();
            }
        });
    }

    /**
     * Used to delete more values from database.
     *
     * @param valueList Values which will be deleted from database.
     * @param callback Callback which will manage onSuccess() action if deletion is made, or
     *                 onFailure() action if deletion failed.
     * */
    public void deleteAll(@NonNull List<T> valueList, @Nullable DataCallbacks.General callback) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            int numberOfAffectedRows = dao.deleteAll(valueList);
            if(callback == null){
                return;
            }

            if(numberOfAffectedRows != valueList.size()){
                Timber.e(Logs.UNEXPECTED_ERROR + " rows " + numberOfAffectedRows + " were deleted");
                callback.onFailure();
            }
            else{
                callback.onSuccess();
            }
        });
    }

}
