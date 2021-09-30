package com.smart_learn.data.repository.expression;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.dao.ExpressionDao;
import com.smart_learn.data.room.db.AppRoomDatabase;
import com.smart_learn.data.room.entities.Expression;
import com.smart_learn.data.room.repository.BasicRoomRepository;
import com.smart_learn.core.helpers.ApplicationController;

import java.util.List;

public class GuestExpressionRepository extends BasicRoomRepository<Expression, ExpressionDao> {

    private static GuestExpressionRepository instance;

    private GuestExpressionRepository() {
        // no need for db instance in class because communication will be made using dao interface
        super(AppRoomDatabase.getDatabaseInstance(ApplicationController.getInstance()).expressionDao());
    }

    public static GuestExpressionRepository getInstance() {
        if(instance == null){
            instance = new GuestExpressionRepository();
        }
        return instance;
    }

    public LiveData<List<Expression>> getCurrentLessonLiveExpressions(int currentLessonId){
        return dao.getLessonLiveExpressions(currentLessonId);
    }

    public List<Expression> getLessonExpressions(int lessonId){
        return dao.getLessonExpressions(lessonId);
    }

    public LiveData<Expression> getSampleLiveExpression(int expressionId) {
        return dao.getSampleLiveExpression(expressionId);
    }

    public Expression getSampleExpression(int expressionId) {
        return dao.getSampleExpression(expressionId);
    }

    public void deleteSelectedItems(int lessonId){
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            dao.deleteSelectedItems(lessonId);
        });
    }

    public void updateSelectAll(boolean isSelected, int lessonId){
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            dao.updateSelectAll(isSelected, lessonId);
        });
    }

    public LiveData<Integer> getLiveSelectedItemsCount(int lessonId){ return dao.getLiveSelectedItemsCount(lessonId); }

    public LiveData<Integer> getLiveNumberOfExpressions(){
        return dao.getLiveNumberOfExpressions();
    }

    public LiveData<Integer> getLiveNumberOfExpressionsForSpecificLesson(int lessonId){
        return dao.getLiveNumberOfExpressionsForSpecificLesson(lessonId);
    }

    public int getNumberOfExpressionsForSpecificLesson(int lessonId){
        return dao.getNumberOfExpressionsForSpecificLesson(lessonId);
    }

    public void deleteAll(int lessonId, @Nullable DataCallbacks.General callback) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            dao.deleteAll(lessonId);
            if(callback == null){
                return;
            }
            callback.onSuccess();
        });
    }
}
