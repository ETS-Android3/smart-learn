package com.smart_learn.core.services;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smart_learn.core.utilities.CoreCallbacks;
import com.smart_learn.data.helpers.BackupHelper;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.repository.BasicRoomRepository;
import com.smart_learn.core.utilities.Logs;
import com.smart_learn.data.room.entities.helpers.BackupStatus;
import com.smart_learn.data.room.entities.helpers.DocumentMetadata;

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


    private boolean validValue(T value){
        if(value == null){
            Timber.w("value is null");
            return false;
        }

        if(!(value instanceof BackupHelper)){
            Timber.w("value is not instance of BackupHelper");
            return false;
        }

        return true;
    }


    private boolean validDocumentMetadata(DocumentMetadata documentMetadata){
        if(documentMetadata == null){
            Timber.w("documentMetadata is null");
            return false;
        }

        if(documentMetadata.getBackupStatus() == null){
            Timber.w("backupStatus is null");
            return false;
        }

        return true;
    }


    private boolean validInsertion(DocumentMetadata documentMetadata) {
        // When insertion is made we should see a new clean object with false values for following
        // flags: isAdded == false, isUpdated == false, isDeleted == false.
        //
        // If item is marked for addition means that object already exists in db and should be
        // updated instead.
        // If item is marked for update means that object already exists in db and can not be added
        // again.
        // If item is marked for deletion means that object already exists in db and will be deleted.

        if(!validDocumentMetadata(documentMetadata)){
            return false;
        }

        BackupStatus backupStatus = documentMetadata.getBackupStatus();
        if(backupStatus.isAdded()){
            Timber.w("This item is marked as added, so item already exist. A new insertion can not be done");
            return false;
        }

        if(backupStatus.isUpdated()){
            Timber.w("This item is marked as updated, so item already exist. A new insertion can not be done");
            return false;
        }

        if(backupStatus.isDeleted()){
            Timber.w("This item is marked for deletion. Insertion can not be done");
            return false;
        }

        return true;
    }


    /**
     * Used to insert one value in database.
     *
     * @param value Value which will be inserted in database.
     * @param callback Callback which will manage onSuccess() action if insertion is made, or
     *                 onFailure() action if insertion failed.
     * */
    public void insert(T value, @Nullable CoreCallbacks.InsertUpdateDeleteCallback<T> callback) {
        if(!validValue(value) || !validInsertion(((BackupHelper)value).getDocumentMetadataObjectReference())){
            if(callback != null){
                callback.onFailure(value);
            }
            return;
        }

        // mark item as added
        ((BackupHelper)value).getDocumentMetadataObjectReference().getBackupStatus().setAdded(true);

        // and try to insert value in local db
        basicRoomRepository.insert(value, new DataCallbacks.InsertUpdateDeleteCallback<T>() {
            @Override
            public void onSuccess(@NonNull @NotNull T value) {
                if(callback != null){
                    callback.onSuccess(value);
                }
            }

            @Override
            public void onFailure(@NonNull @NotNull T value) {
                // reset flag, because insertion failed
                ((BackupHelper)value).getDocumentMetadataObjectReference().getBackupStatus().setAdded(false);

                if(callback != null){
                    callback.onFailure(value);
                }
            }
        });
    }


    private boolean validUpdate(DocumentMetadata documentMetadata) {
        // When update is made we should see only if item is marked for deletion.
        //
        // If item is marked for addition is no problem, because can be updated.
        // If item is marked for update is no problem because item can be updated again.
        // If item is marked for deletion then item will be deleted and update has no sense.

        if(!validDocumentMetadata(documentMetadata)){
            return false;
        }

        BackupStatus backupStatus = documentMetadata.getBackupStatus();
        if(backupStatus.isDeleted()){
            Timber.w("This item is marked for deletion. Item can not be updated");
            return false;
        }

        return true;
    }


    /**
     * Used to update one value in database.
     *
     * @param value Value which will be updated in database.
     * @param callback Callback which will manage onSuccess() action if update is made, or
     *                 onFailure() action if update failed.
     * */
    public void update(T value, @Nullable CoreCallbacks.InsertUpdateDeleteCallback<T> callback) {
        if(!validValue(value) || !validUpdate(((BackupHelper)value).getDocumentMetadataObjectReference())){
            if(callback != null){
                callback.onFailure(value);
            }
            return;
        }

        // used to restore values if update fails
        final long previousUpdateTime = ((BackupHelper) value).getDocumentMetadataObjectReference().getModifiedAt();

        // If item is marked as added, then keep isAdded flag because that means that item was not
        // yet synced (item does not exists in backup). Otherwise mark item as updated.
        final boolean isMarkedAsAdded = ((BackupHelper) value).getDocumentMetadataObjectReference().getBackupStatus().isAdded();
        if(!isMarkedAsAdded){
            // If item is marked as updated, setting flags again will have no effect, but if item
            // is not marked as updated flag must be set.
            ((BackupHelper)value).getDocumentMetadataObjectReference().getBackupStatus().setUpdated(true);
        }

        // Set a new timestamp for current update time, even if item is marked with isAdded because
        // these flags are used only for backup sync, so update time must be recorded always.
        ((BackupHelper)value).getDocumentMetadataObjectReference().setModifiedAt(System.currentTimeMillis());

        // and try to update value
        basicRoomRepository.update(value, new DataCallbacks.InsertUpdateDeleteCallback<T>() {
            @Override
            public void onSuccess(@NonNull @NotNull T value) {
                if(callback != null){
                    callback.onSuccess(value);
                }
            }

            @Override
            public void onFailure(@NonNull @NotNull T value) {
                // Mark item as not updated, because update failed. If flag isAdded was true
                // then will be preserved, because was not changed.
                ((BackupHelper)value).getDocumentMetadataObjectReference().getBackupStatus().setUpdated(false);

                // reset previous update time
                ((BackupHelper)value).getDocumentMetadataObjectReference().setModifiedAt(previousUpdateTime);

                if(callback != null){
                    callback.onFailure(value);
                }
            }
        });
    }


    private boolean validMarkDeletion(DocumentMetadata documentMetadata) {
        // If item is marked already for deletion then item can not be marked again for deletion.
        //
        // If item is marked for addition is no problem, and will be directly deleted because,
        // item is not synced with backup and can be directly deleted.
        //
        // If item is marked for update is no problem because item will be marked as deleted, because
        // exist in backup also, and must be deleted only after backup is made.

        if(!validDocumentMetadata(documentMetadata)){
            return false;
        }

        BackupStatus backupStatus = documentMetadata.getBackupStatus();
        if(backupStatus.isDeleted()){
            Timber.w("This item is marked for deletion. Item can not be marked again");
            return false;
        }

        return true;
    }


    /**
     * Used to set 'isDeleted' flag to true for one value in database. This function will delete a
     * value only if flag isAdded is true and sync was not made (isSync flag is false).
     *
     * @param value Value which will be marked as deleted in database.
     * @param callback Callback which will manage onSuccess() action if setting flag is made, or
     *                 onFailure() action if setting flag failed.
     * */
    public void markDeleteOrDelete(T value, @Nullable CoreCallbacks.InsertUpdateDeleteCallback<T> callback) {
        if(!validValue(value) || !validMarkDeletion(((BackupHelper)value).getDocumentMetadataObjectReference())){
            if(callback != null){
                callback.onFailure(value);
            }
            return;
        }

        // If item is marked as added then is not yet synced (does not exists in backup) and can be
        // directly deleted.
        if(((BackupHelper) value).getDocumentMetadataObjectReference().getBackupStatus().isAdded()){
            delete(value, callback);
            return;
        }

        // used to restore values if mark for deletion fails
        final boolean previousIsUpdatedFlag = ((BackupHelper) value).getDocumentMetadataObjectReference().getBackupStatus().isUpdated();

        // Here item was already synced (so exist in backup), and that means that item will be marked
        // as deleted in order to be deleted from backup also. Only after will be deleted from backup,
        // will be deleted from local db.

        // If item was updated does not matter because will be deleted.
        ((BackupHelper)value).getDocumentMetadataObjectReference().getBackupStatus().setUpdated(false);
        ((BackupHelper)value).getDocumentMetadataObjectReference().getBackupStatus().setDeleted(true);

        // Item will not be directly deleted from db. Only will be updated with new flags.
        basicRoomRepository.update(value, new DataCallbacks.InsertUpdateDeleteCallback<T>() {
            @Override
            public void onSuccess(@NonNull @NotNull T value) {
                if(callback != null){
                    callback.onSuccess(value);
                }
            }

            @Override
            public void onFailure(@NonNull @NotNull T value) {
                // restore flags, because update failed, and was not marked for deletion
                ((BackupHelper)value).getDocumentMetadataObjectReference().getBackupStatus().setUpdated(previousIsUpdatedFlag);
                ((BackupHelper)value).getDocumentMetadataObjectReference().getBackupStatus().setDeleted(false);

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
        // Here we care only if value is not null, so we do not check for entire validValue(...)
        // and for validMarkDeletion(...).
        if(value == null){
            Timber.w("value is null");
            if(callback != null){
                callback.onFailure(null);
            }
            return;
        }

        basicRoomRepository.delete(value, new DataCallbacks.InsertUpdateDeleteCallback<T>() {
            @Override
            public void onSuccess(@NonNull @NotNull T value) {
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
