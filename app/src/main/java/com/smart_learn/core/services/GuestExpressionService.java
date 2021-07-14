package com.smart_learn.core.services;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.smart_learn.core.exceptions.TODO;
import com.smart_learn.core.services.helpers.BasicRoomService;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.repository.GuestExpressionRepository;
import com.smart_learn.data.room.entities.Expression;

import org.jetbrains.annotations.NotNull;

public class GuestExpressionService extends BasicRoomService<Expression, GuestExpressionRepository> {

    private static GuestExpressionService instance;

    private GuestExpressionService() {
        super(GuestExpressionRepository.getInstance());
    }

    public static GuestExpressionService getInstance() {
        if(instance == null){
            instance = new GuestExpressionService();
        }
        return instance;
    }

    public LiveData<Integer> getLiveNumberOfExpressions(){
        return repositoryInstance.getLiveNumberOfExpressions();
    }

    @Override
    protected boolean isItemValid(Expression item) {
        throw new TODO("not implemented");
    }
}
