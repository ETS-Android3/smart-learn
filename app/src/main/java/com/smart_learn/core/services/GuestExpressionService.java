package com.smart_learn.core.services;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.smart_learn.core.services.helpers.BasicRoomService;
import com.smart_learn.data.repository.GuestExpressionRepository;
import com.smart_learn.data.room.entities.Expression;

public class GuestExpressionService extends BasicRoomService<Expression, GuestExpressionRepository> {

    public GuestExpressionService(Application application){
        super(new GuestExpressionRepository(application));
    }

    public LiveData<Integer> getLiveNumberOfExpressions(){
        return repository.getLiveNumberOfExpressions();
    }
}
