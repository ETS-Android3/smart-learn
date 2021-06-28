package com.smart_learn.data.room.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import java.util.List;

public interface BasicDao <T> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(T value);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insertAll(List<T> values);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    int update(T value);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    int updateAll(List<T> items);

    @Delete
    int delete(T value);

    @Delete
    int deleteAll(List<T> items);
}
