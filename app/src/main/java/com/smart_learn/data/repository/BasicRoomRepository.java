package com.smart_learn.data.repository;

import android.util.Log;

import com.smart_learn.data.models.room.dao.BasicDao;
import com.smart_learn.data.models.room.db.AppRoomDatabase;
import com.smart_learn.core.utilities.Logs;

public abstract class BasicRoomRepository <T> {

    protected BasicDao<T> basicDao;

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

}
