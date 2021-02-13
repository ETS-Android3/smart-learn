package com.smart_learn.data.models.room.dao;

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

    @Delete
    void delete(T value);
}
