package com.smart_learn.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.smart_learn.data.room.dao.ExpressionDao;
import com.smart_learn.data.room.db.AppRoomDatabase;
import com.smart_learn.data.room.entities.Expression;
import com.smart_learn.data.room.repository.BasicRoomRepository;

public class GuestExpressionRepository extends BasicRoomRepository<Expression, ExpressionDao> {

    public GuestExpressionRepository(Application application) {
        // no need for db instance in class because communication will be made using dao interface
        super(AppRoomDatabase.getDatabaseInstance(application).expressionDao());
    }

    public LiveData<Integer> getLiveNumberOfExpressions(){
        return dao.getLiveNumberOfExpressions();
    }
}
