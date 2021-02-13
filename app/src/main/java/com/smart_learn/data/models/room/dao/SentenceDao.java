package com.smart_learn.data.models.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.smart_learn.core.config.RoomConfig;
import com.smart_learn.data.models.room.entities.Sentence;

import java.util.List;

@Dao
public interface SentenceDao extends BasicDao<Sentence> {

    @Transaction
    @Query("SELECT * FROM " + RoomConfig.SENTENCES_TABLE)
    LiveData<List<Sentence>> getAllSentences();
}


