package com.smart_learn.data.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.smart_learn.data.room.db.AppRoomDatabase;
import com.smart_learn.data.room.entities.Expression;

import java.util.List;

@Dao
public interface ExpressionDao extends BasicDao<Expression> {

    @Transaction
    @Query("SELECT * FROM " + AppRoomDatabase.EXPRESSIONS_TABLE)
    LiveData<List<Expression>> getAllExpressions();

    @Query("SELECT COUNT(id) FROM " + AppRoomDatabase.EXPRESSIONS_TABLE + " WHERE is_deleted == 0")
    LiveData<Integer> getLiveNumberOfExpressions();
}

