package com.smart_learn.data.guest.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.smart_learn.data.guest.room.dao.helpers.BasicDao;
import com.smart_learn.data.guest.room.db.AppRoomDatabase;
import com.smart_learn.data.guest.room.entitites.Word;

import java.util.List;

@Dao
public interface WordDao extends BasicDao<Word> {

    @Query("SELECT * FROM " + AppRoomDatabase.WORDS_TABLE + " WHERE id == :wordId")
    LiveData<Word> getSampleLiveWord(int wordId);

    @Query("SELECT * FROM " + AppRoomDatabase.WORDS_TABLE + " WHERE id == :wordId")
    Word getSampleWord(int wordId);

    @Transaction
    @Query("SELECT * FROM " + AppRoomDatabase.WORDS_TABLE + " WHERE fk_lesson_id == :lessonId")
    LiveData<List<Word>> getLessonLiveWords(int lessonId);

    @Transaction
    @Query("SELECT * FROM " + AppRoomDatabase.WORDS_TABLE + " WHERE fk_lesson_id == :lessonId")
    List<Word> getLessonWords(int lessonId);

    @Query("DELETE FROM " + AppRoomDatabase.WORDS_TABLE + " WHERE isSelected AND fk_lesson_id == :lessonId")
    void deleteSelectedItems(int lessonId);

    @Query("UPDATE " + AppRoomDatabase.WORDS_TABLE + " SET isSelected = :isSelected WHERE fk_lesson_id == :lessonId")
    void updateSelectAll(boolean isSelected, int lessonId);

    @Query("SELECT COUNT(id) FROM " + AppRoomDatabase.WORDS_TABLE + " WHERE isSelected AND fk_lesson_id == :lessonId")
    LiveData<Integer> getLiveSelectedItemsCount(int lessonId);

    @Query("SELECT COUNT(id) FROM " + AppRoomDatabase.WORDS_TABLE)
    LiveData<Integer> getLiveNumberOfWords();

    @Query("SELECT COUNT(id) FROM " + AppRoomDatabase.WORDS_TABLE + " WHERE fk_lesson_id == :lessonId")
    LiveData<Integer> getLiveNumberOfWordsForSpecificLesson(int lessonId);

    @Query("SELECT COUNT(id) FROM " + AppRoomDatabase.WORDS_TABLE + " WHERE fk_lesson_id == :lessonId")
    int getNumberOfWordsForSpecificLesson(int lessonId);

    @Query("DELETE FROM " + AppRoomDatabase.WORDS_TABLE + " WHERE fk_lesson_id == :lessonId" )
    int deleteAll(int lessonId);
}

