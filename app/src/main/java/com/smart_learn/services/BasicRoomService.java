package com.smart_learn.services;

import android.util.Log;

import com.smart_learn.repository.BasicRoomRepository;
import com.smart_learn.utilities.Logs;

public abstract class BasicRoomService <T> {

    protected BasicRoomRepository<T> basicRoomRepository;

    public void insert(T value) {

        if (basicRoomRepository == null){
            Log.e(Logs.UNEXPECTED_ERROR,Logs.FUNCTION + "[insert in BasicRoomService] basicRoomRepository is null. " +
                    "Value was not inserted.");
            return;
        }

        basicRoomRepository.insert(value);
    }

    public void update(T value) {

        if (basicRoomRepository == null){
            Log.e(Logs.UNEXPECTED_ERROR,Logs.FUNCTION + "[update in BasicRoomService] basicRoomRepository is null. " +
                    "Value was not updated.");
            return;
        }

        basicRoomRepository.update(value);
    }

    public void delete(T value) {

        if (basicRoomRepository == null){
            Log.e(Logs.UNEXPECTED_ERROR,Logs.FUNCTION + "[delete in BasicRoomService] basicRoomRepository is null. " +
                    "Value was not deleted.");
            return;
        }

        basicRoomRepository.delete(value);
    }
}
