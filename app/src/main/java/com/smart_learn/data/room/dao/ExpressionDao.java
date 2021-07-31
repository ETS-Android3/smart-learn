package com.smart_learn.data.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.smart_learn.data.room.db.AppRoomDatabase;
import com.smart_learn.data.room.entities.Expression;

import java.util.List;

@Dao
public interface ExpressionDao extends BasicDao<Expression> {

    @Query("SELECT * FROM " + AppRoomDatabase.EXPRESSIONS_TABLE + " WHERE id == :expressionId")
    LiveData<Expression> getSampleLiveExpression(int expressionId);

    @Transaction
    @Query("SELECT * FROM " + AppRoomDatabase.EXPRESSIONS_TABLE + " WHERE fk_lesson_id == :lessonId")
    LiveData<List<Expression>> getLessonLiveExpressions(int lessonId);

    @Transaction
    @Query("SELECT * FROM " + AppRoomDatabase.EXPRESSIONS_TABLE + " WHERE fk_lesson_id == :lessonId")
    List<Expression> getLessonExpressions(int lessonId);

    @Query("DELETE FROM " + AppRoomDatabase.EXPRESSIONS_TABLE + " WHERE isSelected AND fk_lesson_id == :lessonId")
    void deleteSelectedItems(int lessonId);

    @Query("UPDATE " + AppRoomDatabase.EXPRESSIONS_TABLE + " SET isSelected = :isSelected WHERE fk_lesson_id == :lessonId")
    void updateSelectAll(boolean isSelected, int lessonId);

    @Query("SELECT COUNT(id) FROM " + AppRoomDatabase.EXPRESSIONS_TABLE + " WHERE isSelected AND fk_lesson_id == :lessonId")
    LiveData<Integer> getLiveSelectedItemsCount(int lessonId);

    @Query("SELECT COUNT(id) FROM " + AppRoomDatabase.EXPRESSIONS_TABLE)
    LiveData<Integer> getLiveNumberOfExpressions();

    @Query("SELECT COUNT(id) FROM " + AppRoomDatabase.EXPRESSIONS_TABLE + " WHERE fk_lesson_id == :lessonId")
    LiveData<Integer> getLiveNumberOfExpressionsForSpecificLesson(int lessonId);

    @Query("SELECT COUNT(id) FROM " + AppRoomDatabase.EXPRESSIONS_TABLE + " WHERE fk_lesson_id == :lessonId")
    int getNumberOfExpressionsForSpecificLesson(int lessonId);

    @Query("DELETE FROM " + AppRoomDatabase.EXPRESSIONS_TABLE + " WHERE fk_lesson_id == :lessonId" )
    int deleteAll(int lessonId);
}

