package com.smart_learn.data.models.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.smart_learn.config.RoomConfig;
import com.smart_learn.data.models.room.entities.Word;

import java.util.List;

@Dao
public interface WordDao extends BasicDao<Word>  {

    @Query("SELECT * FROM " + RoomConfig.WORDS_TABLE + " WHERE word LIKE :word")
    Word getSampleWord(String word);

    @Query("SELECT * FROM " + RoomConfig.WORDS_TABLE + " WHERE word LIKE :word AND fkLessonId = :lessonId")
    Word getSampleWord(String word, long lessonId);

    @Transaction
    @Query("SELECT * FROM " + RoomConfig.WORDS_TABLE)
    LiveData<List<Word>> getAllLiveWords();
}
