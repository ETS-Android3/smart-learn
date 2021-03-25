package com.smart_learn.data.models.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.smart_learn.core.config.RoomConfig;
import com.smart_learn.data.models.room.entities.Lesson;
import com.smart_learn.data.models.room.entities.Word;

import java.util.List;

@Dao
public interface WordDao extends BasicDao<Word>  {

    @Query("SELECT * FROM " + RoomConfig.WORDS_TABLE + " WHERE wordId = :wordId")
    LiveData<Word> getSampleLiveWord(long wordId);

    @Query("SELECT * FROM " + RoomConfig.WORDS_TABLE + " WHERE word LIKE :word")
    Word getSampleWord(String word);

    @Query("SELECT * FROM " + RoomConfig.WORDS_TABLE + " WHERE word LIKE :word AND fkLessonId = :lessonId")
    Word getSampleWord(String word, long lessonId);

    @Transaction
    @Query("SELECT * FROM " + RoomConfig.WORDS_TABLE + " WHERE fkLessonId = :lessonId")
    LiveData<List<Word>> getLessonLiveWords(long lessonId);

    @Query("DELETE FROM " + RoomConfig.WORDS_TABLE + " WHERE isSelected AND fkLessonId = :lessonId")
    void deleteSelectedItems(long lessonId);

    @Query("UPDATE " + RoomConfig.WORDS_TABLE + " SET isSelected = :isSelected WHERE fkLessonId = :lessonId")
    void updateSelectAll(boolean isSelected, long lessonId);

    @Query("SELECT COUNT(wordId) FROM " + RoomConfig.WORDS_TABLE + " WHERE isSelected AND fkLessonId = :lessonId")
    LiveData<Integer> getLiveSelectedItemsCount(long lessonId);

    @Query("SELECT COUNT(wordId) FROM " + RoomConfig.WORDS_TABLE + " WHERE fkLessonId = :lessonId")
    LiveData<Integer> getLiveItemsNumber(long lessonId);
}
