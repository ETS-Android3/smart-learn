package com.smart_learn.models.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.smart_learn.config.RoomConfig;
import com.smart_learn.models.room.entities.Word;

import java.util.List;

@Dao
public interface WordDao extends BasicDao<Word>  {

    @Transaction
    @Query("SELECT * FROM " + RoomConfig.WORDS_TABLE)
    LiveData<List<Word>> getAllWords();
}
