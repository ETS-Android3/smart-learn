package com.smart_learn.core.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.smart_learn.core.services.helpers.BasicRoomService;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.data.repository.GuestExpressionRepository;
import com.smart_learn.data.room.entities.Expression;
import com.smart_learn.data.room.entities.helpers.Translation;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class GuestExpressionService extends BasicRoomService<Expression, GuestExpressionRepository> {

    private static GuestExpressionService instance;

    private GuestExpressionService() {
        super(GuestExpressionRepository.getInstance());
    }

    public static GuestExpressionService getInstance() {
        if(instance == null){
            instance = new GuestExpressionService();
        }
        return instance;
    }

    public LiveData<List<Expression>> getCurrentLessonLiveExpressions(int currentLessonId){
        return repositoryInstance.getCurrentLessonLiveExpressions(currentLessonId);
    }

    @NonNull
    public List<Expression> getCurrentLessonSampleExpressions(int currentLessonId){
        List<Expression> tmp = repositoryInstance.getCurrentLessonLiveExpressions(currentLessonId).getValue();
        if(tmp == null){
            return new ArrayList<>();
        }
        return tmp;
    }

    public LiveData<Expression> getSampleLiveExpression(int expressionId) {
        return repositoryInstance.getSampleLiveExpression(expressionId);
    }

    public void deleteSelectedItems(int lessonId){ repositoryInstance.deleteSelectedItems(lessonId); }

    public void updateSelectAll(boolean isSelected, int lessonId){ repositoryInstance.updateSelectAll(isSelected,lessonId); }

    public LiveData<Integer> getLiveSelectedItemsCount(int lessonId){ return repositoryInstance.getLiveSelectedItemsCount(lessonId); }

    public LiveData<Integer> getLiveNumberOfExpressions(){
        return repositoryInstance.getLiveNumberOfExpressions();
    }

    public LiveData<Integer> getLiveNumberOfExpressionsForSpecificLesson(int lessonId){
        return repositoryInstance.getLiveNumberOfExpressionsForSpecificLesson(lessonId);
    }

    public int getNumberOfExpressionsForSpecificLesson(int lessonId){
        return repositoryInstance.getNumberOfExpressionsForSpecificLesson(lessonId);
    }

    @Override
    protected boolean isItemValid(Expression item) {
        // TODO: refactor this method

        if(item == null){
            Timber.w("item is null");
            return false;
        }

        // check expression
        if (item.getExpression() == null || item.getExpression().isEmpty()) {
            Timber.w("expression is null or empty");
            return false;
        }

        if (item.getExpression().length() > DataUtilities.Limits.MAX_EXPRESSION) {
            Timber.w("expression is too big [" + item.getExpression().length() + "]");
            return false;
        }

        // check language
        if(item.getLanguage() == null){
            item.setLanguage("");
        }

        if(item.getLanguage().length() > DataUtilities.Limits.MAX_LANGUAGE){
            Timber.w("language is too big [" + item.getLanguage().length() + "]");
            return false;
        }

        // check notes
        if(item.getNotes() == null){
            item.setNotes("");
        }

        if(item.getNotes().length() > DataUtilities.Limits.MAX_NOTES){
            Timber.w("notes is too big [" + item.getNotes().length() + "]");
            return false;
        }


        // check translations
        if(item.getTranslations() == null){
            item.setTranslations(new ArrayList<>());
        }

        ArrayList<Translation> list = item.getTranslations();
        for(Translation translation : list){
            // check translation
            if(translation.getTranslation() == null){
                translation.setTranslation("");
            }

            if(translation.getTranslation().length() > DataUtilities.Limits.MAX_EXPRESSION_TRANSLATION){
                Timber.w("translation is too big [" + translation.getTranslation().length() + "]");
                return false;
            }

            // check language
            if(translation.getLanguage() == null){
                translation.setLanguage("");
            }

            if(translation.getLanguage().length() > DataUtilities.Limits.MAX_LANGUAGE){
                Timber.w("language is too big [" + translation.getLanguage().length() + "]");
                return false;
            }
        }

        return true;
    }

    public void deleteAll(int lessonId, @Nullable DataCallbacks.General callback) {
        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Values deleted",
                    "Deletion for values failed");
        }

        repositoryInstance.deleteAll(lessonId, callback);
    }
}
