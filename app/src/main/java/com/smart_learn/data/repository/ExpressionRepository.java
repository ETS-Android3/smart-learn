package com.smart_learn.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.smart_learn.data.room.dao.ExpressionDao;
import com.smart_learn.data.room.db.AppRoomDatabase;
import com.smart_learn.data.room.entities.Expression;
import com.smart_learn.data.room.repository.BasicRoomRepository;

public class ExpressionRepository extends BasicRoomRepository<Expression> {

    private final ExpressionDao expressionDao;

    public ExpressionRepository(Application application) {
        // no need for db instance in class because communication will be made using dao interface
        AppRoomDatabase db = AppRoomDatabase.getDatabaseInstance(application);

        // this is used to communicate with db
        expressionDao = db.expressionDao();

        // set dao in super class
        super.basicDao = expressionDao;
    }

    public LiveData<Integer> getLiveNumberOfExpressions(){
        return expressionDao.getLiveNumberOfExpressions();
    }
}
