package com.smart_learn.presenter.guest.activities.notebook.fragments.home_expression;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.R;
import com.smart_learn.core.guest.services.GuestExpressionService;
import com.smart_learn.data.common.helpers.DataCallbacks;
import com.smart_learn.data.guest.room.entitites.Expression;
import com.smart_learn.data.common.entities.Translation;
import com.smart_learn.presenter.common.activities.notebook.fragments.home_expression.HomeExpressionViewModel;
import com.smart_learn.core.common.helpers.ApplicationController;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Getter;
import timber.log.Timber;

public class GuestHomeExpressionViewModel extends HomeExpressionViewModel {

    @Getter
    protected final MutableLiveData<Expression> liveExpression;

    public GuestHomeExpressionViewModel(@NonNull @NotNull Application application) {
        super(application);
        liveExpression = new MutableLiveData<>(Expression.generateEmptyObject());
    }

    public void setLiveExpression(Expression expression){
        if(expression == null){
            Timber.w("expression is null");
            return;
        }
        liveIsOwner.setValue(true);
        liveExpression.setValue(expression);
        liveExpressionValue.setValue(expression.getExpression());
        liveExpressionNotes.setValue(expression.getNotes());
        allTranslations = expression.getTranslations();
        if(adapter != null){
            adapter.setItems(allTranslations);
        }
    }

    @Override
    protected void saveExpressionValue(String newValue) {
        Expression expression = liveExpression.getValue();
        if(expression == null){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_update_expression));
            return;
        }

        expression.setExpression(newValue);

        GuestExpressionService.getInstance().update(expression, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.success_update_expression));
            }
            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_update_expression));
            }
        });
    }

    @Override
    protected void saveExpressionNotes(String newValue) {
        Expression expression = liveExpression.getValue();
        if(expression == null){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_update_notes));
            return;
        }

        expression.setNotes(newValue);

        GuestExpressionService.getInstance().update(expression, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.success_update_notes));
            }
            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_update_notes));
            }
        });
    }

    @Override
    protected void addExpressionTranslation(Translation translation) {
        Expression expression = liveExpression.getValue();
        if(expression == null){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_add_translations));
            return;
        }

        expression.getTranslations().add(translation);

        GuestExpressionService.getInstance().update(expression, new DataCallbacks.General() {
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
        Expression expression = liveExpression.getValue();
        if(expression == null){
            callback.onFailure();
            return;
        }

        expression.setTranslations(newList);

        GuestExpressionService.getInstance().update(expression, new DataCallbacks.General() {
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