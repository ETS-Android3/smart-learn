package com.smart_learn.data.models.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.smart_learn.core.config.RoomConfig;
import com.smart_learn.data.models.room.entities.Expression;

import java.util.List;

@Dao
public interface ExpressionDao extends BasicDao<Expression> {

    @Transaction
    @Query("SELECT * FROM " + RoomConfig.EXPRESSIONS_TABLE)
    LiveData<List<Expression>> getAllExpressions();
}

