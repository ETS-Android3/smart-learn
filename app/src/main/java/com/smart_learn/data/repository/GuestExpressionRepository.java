package com.smart_learn.data.repository;

import androidx.lifecycle.LiveData;

import com.smart_learn.data.room.dao.ExpressionDao;
import com.smart_learn.data.room.db.AppRoomDatabase;
import com.smart_learn.data.room.entities.Expression;
import com.smart_learn.data.room.repository.BasicRoomRepository;
import com.smart_learn.presenter.helpers.ApplicationController;

public class GuestExpressionRepository extends BasicRoomRepository<Expression, ExpressionDao> {

    private static GuestExpressionRepository instance;

    private GuestExpressionRepository() {
        // no need for db instance in class because communication will be made using dao interface
        super(AppRoomDatabase.getDatabaseInstance(ApplicationController.getInstance()).expressionDao());
    }

    public static GuestExpressionRepository getInstance() {
        if(instance == null){
            instance = new GuestExpressionRepository();
        }
        return instance;
    }

    public LiveData<Integer> getLiveNumberOfExpressions(){
        return dao.getLiveNumberOfExpressions();
    }
}
