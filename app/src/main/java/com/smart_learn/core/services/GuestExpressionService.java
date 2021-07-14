package com.smart_learn.core.services;

import androidx.lifecycle.LiveData;

import com.smart_learn.core.exceptions.TODO;
import com.smart_learn.core.services.helpers.BasicRoomService;
import com.smart_learn.data.repository.GuestExpressionRepository;
import com.smart_learn.data.room.entities.Expression;

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

    public LiveData<Integer> getLiveNumberOfExpressionsForSpecificLesson(int lessonId){
        return repositoryInstance.getLiveNumberOfExpressionsForSpecificLesson(lessonId);
    }
    public int getNumberOfExpressionsForSpecificLesson(int lessonId){
        return repositoryInstance.getNumberOfExpressionsForSpecificLesson(lessonId);
    }

    @Override
    protected boolean isItemValid(Expression item) {
        throw new TODO("not implemented");
    }
}
