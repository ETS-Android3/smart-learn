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
    void insertAll(List<T> values);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void update(T value);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateAll(List<T> items);

    @Delete
    void delete(T value);

    @Delete
    void deleteAll(List<T> items);
}
