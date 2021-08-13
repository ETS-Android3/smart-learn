package com.smart_learn.presenter.activities.notebook.user.fragments.home_expression;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.services.expression.UserExpressionService;
import com.smart_learn.core.services.UserService;
import com.smart_learn.data.firebase.firestore.entities.ExpressionDocument;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.home_expression.HomeExpressionViewModel;
import com.smart_learn.core.helpers.ApplicationController;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserHomeExpressionViewModel extends HomeExpressionViewModel {

    private DocumentSnapshot expressionSnapshot;

    public UserHomeExpressionViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public void setLiveExpression(DocumentSnapshot newSnapshot, ExpressionDocument newExpression){
        expressionSnapshot = newSnapshot;
        liveIsOwner.setValue(newExpression.getDocumentMetadata().getOwner().equals(UserService.getInstance().getUserUid()));
        liveIsFromSharedLesson.setValue(newExpression.isFromSharedLesson());
        liveCreatedBy.setValue(newExpression.getOwnerDisplayName());
        liveExpressionValue.setValue(newExpression.getExpression());
        liveExpressionNotes.setValue(newExpression.getNotes());
        allTranslations = Translation.fromJsonToList(newExpression.getTranslations());
        if(adapter != null){
            adapter.setItems(allTranslations);
        }
    }

    @Override
    protected void saveExpressionValue(String newValue) {
        UserExpressionService.getInstance().updateExpressionValue(newValue, expressionSnapshot, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.succes_update_expression));
            }
            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_update_expression));
            }
        });
    }

    @Override
    protected void saveExpressionNotes(String newValue) {
        UserExpressionService.getInstance().updateExpressionNotes(newValue, expressionSnapshot, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.succes_update_notes));
            }
            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_update_notes));
            }
        });
    }

    @Override
    protected void addExpressionTranslation(Translation translation) {
        ArrayList<Translation> newTranslations = new ArrayList<>(allTranslations);
        newTranslations.add(translation);

        UserExpressionService.getInstance().updateExpressionTranslations(newTranslations, expressionSnapshot, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.success_add_translations));
            }
            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_add_translations));
            }
        });
    }

    @Override
    protected void updateExpressionTranslations(ArrayList<Translation> newList, @NonNull @NotNull DataCallbacks.General callback){
        UserExpressionService.getInstance().updateExpressionTranslations(newList, expressionSnapshot, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }
            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }
}