package com.smart_learn.data.guest.room.dao.helpers;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

public interface BasicDao <T> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(T value);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insertAll(List<T> values);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    int update(T value);

    @Transaction
    @Update(onConflict = OnConflictStrategy.IGNORE)
    int updateAll(List<T> items);

    @Delete
    int delete(T value);

    @Transaction
    @Delete
    int deleteAll(List<T> items);
}
