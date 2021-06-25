package com.smart_learn.core.services;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.smart_learn.data.repository.ExpressionRepository;
import com.smart_learn.data.room.entities.Expression;

public class ExpressionService extends BasicRoomService<Expression> {

    private final ExpressionRepository expressionRepository;

    public ExpressionService(Application application){
        expressionRepository = new ExpressionRepository(application);

        // set super repository
        super.basicRoomRepository = expressionRepository;
    }

    public LiveData<Integer> getLiveNumberOfExpressions(){
        return expressionRepository.getLiveNumberOfExpressions();
    }
}
